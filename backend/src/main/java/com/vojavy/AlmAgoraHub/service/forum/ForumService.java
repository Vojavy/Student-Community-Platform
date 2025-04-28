// src/main/java/com/vojavy/AlmAgoraHub/service/forum/ForumService.java
package com.vojavy.AlmAgoraHub.service.forum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vojavy.AlmAgoraHub.dto.requests.CreateForumPostRequest;
import com.vojavy.AlmAgoraHub.dto.requests.CreateForumRequest;
import com.vojavy.AlmAgoraHub.dto.responses.ForumDetailResponse;
import com.vojavy.AlmAgoraHub.model.UniversityDomain;
import com.vojavy.AlmAgoraHub.model.forum.Forum;
import com.vojavy.AlmAgoraHub.model.forum.ForumPost;
import com.vojavy.AlmAgoraHub.model.forum.ForumStatus;
import com.vojavy.AlmAgoraHub.model.user.Role;
import com.vojavy.AlmAgoraHub.model.user.RoleType;
import com.vojavy.AlmAgoraHub.model.user.User;
import com.vojavy.AlmAgoraHub.repository.forum.ForumRepository;
import com.vojavy.AlmAgoraHub.service.UniversityDomainService;
import com.vojavy.AlmAgoraHub.service.user.RoleService;
import com.vojavy.AlmAgoraHub.service.user.UserService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
public class ForumService {

    private final ForumRepository         forumRepo;
    private final UserService             userService;
    private final UniversityDomainService domainService;
    private final RoleService             roleService;
    private final ForumPostService        forumPostService;
    private final ObjectMapper            objectMapper = new ObjectMapper();

    public ForumService(
            ForumRepository forumRepo,
            UserService userService,
            UniversityDomainService domainService,
            RoleService roleService,
            ForumPostService forumPostService
    ) {
        this.forumRepo        = forumRepo;
        this.userService      = userService;
        this.domainService    = domainService;
        this.roleService      = roleService;
        this.forumPostService = forumPostService;
    }

    public Forum createForum(CreateForumRequest req, Long creatorUserId) {
        User creator = userService.findById(creatorUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UniversityDomain domain = domainService.getDomainByCode(req.getUniversityDomain())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Domain not found"));

        Set<Role> roles;
        if (req.getAllowedRoles() == null || req.getAllowedRoles().isEmpty())
            roles = new HashSet<>(creator.getRoles());
        else
            roles = req.getAllowedRoles().stream()
                .map(roleService::getByName)
                .collect(Collectors.toSet());

        Forum forum = new Forum();
        forum.setName(req.getName());

        try {
            String topicsJson = objectMapper.writeValueAsString(req.getTopics());
            forum.setTopics(topicsJson);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid topics format: expected array of strings",
                    e
            );
        }

        forum.setDescription(req.getDescription());
        forum.setCreatedBy(creator);
        forum.setUniversityDomain(domain);
        forum.setCreatedAt(Instant.now());
        forum.setStatus(req.getStatus());
        forum.setPinned(req.isPinned());
        forum.setPublic(req.isPublic());
        forum.setClosed(req.isClosed());
        forum.setAllowedRoles(roles);

        return forumRepo.save(forum);
    }

    public void updateForum(Integer forumId, CreateForumRequest req, Long actorUserId) {

        User actor = userService.findById(actorUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        Forum forum = forumRepo.findById(forumId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Forum not found"));

        if (!forum.getCreatedBy().equals(actor) && !actor.getRoles().contains(RoleType.ROLE_ADMIN)) {
            throw new ResponseStatusException(FORBIDDEN, "Only creators or admins can update forums");
        };

        Set<Role> roles;
        if (req.getAllowedRoles() == null || req.getAllowedRoles().isEmpty())
            roles = new HashSet<>(actor.getRoles());
        else
            roles = req.getAllowedRoles().stream()
                    .map(roleService::getByName)
                    .collect(Collectors.toSet());


        forum.setName(req.getName());
        forum.setDescription(req.getDescription());
        forum.setStatus(req.getStatus());
        forum.setPinned(req.isPinned());
        forum.setPublic(req.isPublic());
        forum.setClosed(req.isClosed());
        try {
            String topicsJson = objectMapper.writeValueAsString(req.getTopics());
            forum.setTopics(topicsJson);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid topics format: expected array of strings",
                    e
            );
        }
        forum.setAllowedRoles(roles);

        if (req.getUniversityDomain() == null)
            forum.setUniversityDomain(null);
        else {
            UniversityDomain domain = domainService.getDomainByCode(req.getUniversityDomain())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Domain not found"));
            forum.setUniversityDomain(domain);
        }
        forumRepo.save(forum);
    }

    public void deleteForum(Integer forumId, Long actorUserId) {
        User user = userService.findById(actorUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Forum forum = forumRepo.findById(forumId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Forum not found"));

        if (!forum.getCreatedBy().equals(user) && !user.getRoles().contains(RoleType.ROLE_ADMIN)) {
            throw new ResponseStatusException(FORBIDDEN, "Only creators or admins can delete forums");
        }

        forumRepo.delete(forum);
    }

    public Page<Forum> browseForums(
            Long           userId,
            Boolean        isPublic,
            String         domainCode,
            ForumStatus    status,
            String         nameSearch,
            List<String>   topicsFilter,
            String         sort,
            int            page,
            int            size
    ) {
        // 1) try to load user; if not found, anonymous ⇒ only public
        Optional<User> userOpt = userService.findById(userId);
        boolean anonymous = userOpt.isEmpty();
        User   user      = userOpt.orElse(null);

        // 2) resolve domain if passed
        UniversityDomain domain;
        if (domainCode != null && !domainCode.isBlank()) {
            domain = domainService.getDomainByCode(domainCode)
                    .orElseThrow(() -> new ResponseStatusException(
                            BAD_REQUEST, "Domain not found: " + domainCode));
        } else {
            domain = null;
        }

        // 3) fetch all
        List<Forum> all = forumRepo.findAll();

        // 4) in‐memory filter
        List<Forum> filtered = all.stream()
                // a) if anonymous, only public; else respect isPublic
                .filter(f -> anonymous
                        ? f.isPublic()
                        : (isPublic == null || f.isPublic() == isPublic))
                // b) domain match by ID
                .filter(f -> domain == null
                        || f.getUniversityDomain().getId().equals(domain.getId()))
                // c) status
                .filter(f -> status == null || f.getStatus() == status)
                // d) name substring
                .filter(f -> nameSearch == null
                        || f.getName().toLowerCase().contains(nameSearch.toLowerCase()))
                // e) topics JSONB → List<String>
                .filter(f -> {
                    if (topicsFilter == null || topicsFilter.isEmpty()) return true;
                    try {
                        List<String> stored = objectMapper.readValue(
                                f.getTopics(), new TypeReference<List<String>>() {});
                        return stored.stream().anyMatch(topicsFilter::contains);
                    } catch (Exception ex) {
                        return false;
                    }
                })
                // f) hide banned forums unless user is ADMIN
                .filter(f -> {
                    if (f.getStatus() == ForumStatus.banned) {
                        // anonymous always false
                        if (anonymous) return false;
                        // now simply check if user.getRoles() contains the ADMIN enum
                        return user.getRoles().contains(RoleType.ROLE_ADMIN);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 5) sort by createdAt
        Comparator<Forum> byDate = Comparator.comparing(Forum::getCreatedAt);
        if ("newest".equalsIgnoreCase(sort)) {
            byDate = byDate.reversed();
        } else if (!"oldest".equalsIgnoreCase(sort)) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Invalid sort value '" + sort + "'. Use 'newest' or 'oldest'."
            );
        }
        filtered.sort(byDate);

        // 6) paginate
        Pageable pg = PageRequest.of(page, size);
        int start = (int) Math.min(pg.getOffset(), filtered.size());
        int end   = Math.min(start + pg.getPageSize(), filtered.size());
        List<Forum> slice = filtered.subList(start, end);

        return new PageImpl<>(slice, pg, filtered.size());
    }

    public ForumDetailResponse getForumDetails(Integer forumId, Long userId) {
        // 1) load & check basic access
        Forum forum = loadAndCheckForum(forumId, userId);

        // 2) if banned, only ADMINs see it at all (everyone else gets 404)
        if (forum.getStatus() == ForumStatus.banned) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND, "Forum not found"));
            boolean isAdmin = user.getRoles().contains(RoleType.ROLE_ADMIN);
            if (!isAdmin) {
                throw new ResponseStatusException(NOT_FOUND, "Forum not found");
            }
        }

        // 3) build and return the DTO
        return ForumDetailResponse.fromEntity(forum);
    }

    public ForumPost createPost(Long creatorUserId,
                                Integer forumId,
                                CreateForumPostRequest req) {
        Forum forum = loadAndCheckForum(forumId, creatorUserId);

        if (forum.getStatus() != ForumStatus.active) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "Cannot post to a forum in status: " + forum.getStatus());
        }

        User author = userService.findById(creatorUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        // Check enum membership instead of entity compare:
        boolean roleOk = author.getRoles().stream()
                .anyMatch(forum.getAllowedRoles()::contains);
        if (!roleOk) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "User’s role is not permitted to post in this forum");
        }

        ForumPost parent = null;
        if (req.getParentPostId() != null) {
            parent = forumPostService.getPost(forum, req.getParentPostId());
        }

        return forumPostService.createPost(
                forum, author, req.getContent(), parent);
    }

    public Page<ForumPost> listPosts(
            Integer forumId,
            Long    userId,
            Integer parentPostId,
            int     page,
            int     size
    ) {
        Forum forum = loadAndCheckForum(forumId, userId);

        List<ForumPost> all = forumPostService.findAllByForum(forum);
        Map<Integer, List<ForumPost>> children = new HashMap<>();
        all.forEach(p -> {
            Integer key = p.getParentPost() == null ? null : p.getParentPost().getId();
            children.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        });

        List<ForumPost> flat = new ArrayList<>();
        collectPostsDepthFirst(parentPostId, children, flat);

        Pageable pg = PageRequest.of(page, size, Sort.by("createdAt").descending());
        int start = (int) Math.min(pg.getOffset(), flat.size());
        int end   = Math.min(start + pg.getPageSize(), flat.size());
        List<ForumPost> slice = flat.subList(start, end);

        return new PageImpl<>(slice, pg, flat.size());
    }

    /**
     * - if closed  ⇒ only same-domain users
     * - if not public ⇒ only same-domain users
     */
    private Forum loadAndCheckForum(Integer forumId, Long userId) {
        Forum forum = forumRepo.findById(forumId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Forum not found"));

        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        // closed forums: only domain members
        if (forum.isClosed()
                && !forum.getUniversityDomain().getId().equals(user.getDomain().getId())
                && !user.getRoles().contains(RoleType.ROLE_ADMIN)) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "Forum is closed — only domain members may access");
        }

        return forum;
    }

    private void collectPostsDepthFirst(
            Integer parentId,
            Map<Integer, List<ForumPost>> children,
            List<ForumPost> accumulator
    ) {
        List<ForumPost> kids = children.get(parentId);
        if (kids == null) return;
        for (ForumPost c : kids) {
            accumulator.add(c);
            collectPostsDepthFirst(c.getId(), children, accumulator);
        }
    }
}
