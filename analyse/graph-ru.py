import pandas as pd
import matplotlib.pyplot as plt
import os
import re
import textwrap

# =============================================================================
# Словари для перевода (русский → чешский)
# =============================================================================
# Заполните следующие словари согласно вашим данным.
# Полный словарь переводов вопросов из русского файла на чешский
question_translations = {
    "Насколько вы чувствуете, что интегрированы в университетскую жизнь (мероприятия, акции, поездки)? ": 
        "Do jaké míry se cítíte začleněni do univerzitního života (akce, výlety, setkání)? ",
    "Насколько вам просто находить информацию о мероприятиях, которые проходят в университете? ": 
        "Jak snadné pro vás je získávat informace o událostech, které se na univerzitě konají? ",
    "Если вы состоите в университетской группе (клубе) по интересам, насколько легко было найти её и присоединиться? \n(Если не состоите, пропустите вопрос.)  ": 
        "Pokud jste členem univerzitní skupiny (klubu) se společnými zájmy, jak snadné bylo tuto skupinu najít a přidat se k ní? ",
    "Насколько часто вы посещаете университетские мероприятия (культурные, научные, спортивные)? ": 
        "Jak často navštěvujete univerzitní akce (kulturní, vědecké, sportovní)? ",
    "Насколько вы готовы делиться своими учебными материалами (конспектами, презентациями)?": 
        "Do jaké míry jste ochotni sdílet své studijní materiály (poznámky, prezentace)",
    "Если говорить о периоде ДО поступления, насколько вы испытывали трудности в поиске информации о студенческой жизни (общежития, кружки, административные вопросы)? ": 
        "Co se týče období PŘED nástupem na univerzitu, jak výrazné jste pociťovali potíže s vyhledáváním informací o studentském životě (koleje, kroužky, administrativní postupy)? ",
    "Откуда вы чаще всего узнаёте об университетских мероприятиях? (Один основной вариант) ": 
        "Odkud se nejčastěji dozvídáte o univerzitních událostech? (Vyberte hlavní zdroj) ",
    "Какие виды мероприятий вас интересуют? (Выберите все подходящие варианты) ": 
        "O které typy událostí máte největší zájem? (Vyberte všechny vhodné možnosti) ",
    "Состоите ли вы в какой-либо студенческой группе (клубе) по интересам? (Один вариант) ": 
        "Jste členem nějaké studentské skupiny (klubu) se společnými zájmy? (Vyberte jednu možnost) ",
    "С какими основными трудностями вы столкнулись до или в момент поступления? (Выберите все применимые варианты) ": 
        "S jakými hlavními obtížemi jste se potýkali před či při nástupu na univerzitu? (Vyberte všechny, které se vás týkají) ",
    "Что вам больше всего помогло адаптироваться в университете? (Выберите максимум 2 варианта) ": 
        "Co vám nejvíce pomohlo s adaptací na univerzitu? (Vyberte maximálně 2 možnosti) ",
    "Если бы существовала единая университетская платформа (соцсеть), было бы вам проще, если бы объявления о мероприятиях, студенческий форум и маркетплейс находились в одном месте? ": 
        "Pokud by existovala jednotná univerzitní platforma (sociální síť), usnadnilo by vám to, kdyby se oznámení o akcích, studentské fórum a tržiště nacházely na jednom místě? ",
    "Насколько бы вам помогла подобная платформа (онлайн-сообщество) ещё до поступления, чтобы узнать больше о жизни в университете? ": 
        "Do jaké míry by vám pomohla taková platforma (online komunita) ještě před nástupem, abyste se více dozvěděli o univerzitním životě? ",
    "Если бы вы могли обратиться к другим студентам ещё до поступления (через закрытый форум для абитуриентов), помогло бы это вам быстрее адаптироваться? ": 
        "Kdybyste se mohli obrátit na další studenty ještě před nástupem na fakultu (prostřednictvím uzavřeného fóra pro uchazeče), pomohlo by vám to rychleji se adaptovat? ",
    "Хотели бы вы видеть базовую информацию о других студентах (факультет, курс) при взаимодействии на платформе? ": 
        "Chtěli byste mít přístup k základním informacím o ostatních studentech (fakulta, ročník) při vzájemné interakci na platformě? ",
    "Считаете ли вы нужным, чтобы платформа была связана с вашим учебным расписанием (например, показывала грядущие экзамены, дедлайны, события кафедры)? ": 
        "Považujete za užitečné, aby platforma byla propojena s vaším studijním rozvrhem (např. aby ukazovala nadcházející zkoušky, termíny, akce katedry)? ",
    "Стали бы вы активно выкладывать собственные материалы или посты (объявления о продаже вещей, учебные вопросы, события) на такой платформе? ": 
        "Začali byste aktivně zveřejňovat vlastní materiály nebo příspěvky (inzeráty, studijní dotazy, události) na takové platformě? ",
    "Нужна ли вам визуализация кампуса (интерактивная карта корпусов, аудиторий, библиотеки) в той же платформе? ": 
        "Byla by podle vás užitečná vizualizace kampusu (interaktivní mapa budov, knihovny, učeben) přímo v rámci stejné platformy? ",
    "Хотели бы вы, чтобы платформа поддерживала удобный обмен учебными материалами (презентации, заметки) в публичных или закрытых группах? ": 
        "Uvítali byste, kdyby platforma usnadňovala sdílení studijních materiálů (prezentací, poznámek) ve veřejných či uzavřených skupinách?"
}

# Поскольку ответов на вопросы много, для качественного перевода вариантов ответов 
# необходимо отдельно обработать данные каждого вопроса типа 2 и 3, так как они специфичны. 
# Вы можете указать конкретные ответы, которые хотите перевести (или предоставить их списком).


answer_translations = {
    "Нехватка информации о факультетах и учебных программах":
        "Nedostatek informací o fakultách a studijních programech",
    "Отсутствие сообщества / знакомых для обмена опытом":
        "Chybějící komunita / známí pro sdílení zkušeností",
    "Неясность в административных процедурах (документы, сроки)":
        "Nejasnosti v administrativních postupech (dokumenty, termíny)",
    "Социальные сети (Facebook, Instagram)":
        "Sociální sítě (Facebook, Instagram)",
    "Личные контакты и дружеские связи":
        "Osobní kontakty a přátelské vazby",
        "Сложность поиска жилья/общежития":
        "Problém najít bydlení / kolej",

    "Отсутствие знакомых/сообщества для обмена опытом":
        "Chybějící komunita / známí pro sdílení zkušeností",

    "Трудностей не было":
        "Neměl(a) jsem žádné obtíže",

    "рудности с пониманием административных процессов (документы, дедлайны)":
        "Nejasnosti v administrativních postupech (dokumenty, termíny)",

    "Нехватка информации о факультетах и программах":
        "Nedostatek informací o fakultách a studijních programech",

    "Языковой барьер":
        "Jazyková bariéra",

    # Некорректный ответ
    "Я пидор гырлы гырлы горловой":
        "Neplatná odpověď",

     "Нет": "Ne",
    "Да, в одном": "Ano, v jednom",
    "Да, в нескольких": "Ano, v několika",
    "Да": "Ano",
    "Нехватка информации о факультетах и учебных программах":
        "Nedostatek informací o fakultách a studijních programech",
    "Отсутствие сообщества / знакомых для обмена опытом":
        "Chybějící komunita / známí pro sdílení zkušeností",
    "Неясность в административных процедурах (документы, сроки)":
        "Nejasnosti v administrativních postupech (dokumenty, termíny)",
    "Социальные сети (Facebook, Instagram)":
        "Sociální sítě (Facebook, Instagram)",
    "Личные контакты и дружеские связи":
        "Osobní kontakty a přátelské vazby",
    "Сложность поиска жилья/общежития":
        "Problém najít bydlení / kolej",
        "Социальные сети (Facebook, Instagram)":"Sociální sítě (Facebook, Instagram)",
        "Консультации с кураторами, преподавателями":"Konzultace s tutor(y), vyučujícími",
        "Помощь старшекурсников (наставничество)":"Pomoc starších studentů (mentoring)",
        "Официальные сайты/площадки университета":"Oficiální weby/univerzitní portály",
        "Личные знакомства и дружеские связи":"Osobní kontakty a přátelské vazby",
        "Никакие ресурсы особенно не помогли":"Nic z toho mi příliš nepomohlo",
        "Официальный веб-сайт университета":"Oficiální web univerzity",
        "Оффлайн реклама (листовки, объявления, баннеры)":"Offline reklama (letáky, nástěnky, bannery)",
        "От других людей (студентов, преподавателей)":"Od ostatních lidí (spolužáci, vyučující)",
        "Другое (Telegram, WhatsApp и т.п.)":"Jiné (Telegram, WhatsApp apod.)",
        "Культурные (концерты, выставки)":"Kulturní (koncerty, výstavy)",
        "Научные (конференции, семинары, хакатоны)":"Vědecké (konference, semináře, hackathony)",
        "Спортивные (соревнования, секции)":"Sportovní (závody, tréninkové skupiny)",
        "Развлекательные (вечеринки, игры, квизы)":"Zábavné (večírky, kvízy, hry)",
        "Волонтёрские":"Dobrovolnické",
        "Образовательные":"Vzdělávací",
        "Оргии, групповой секс не обязательно с женщинами":"Neplatná odpověď",
        "Не  интересуют":"Nemám zájem",
        "Трудности с пониманием административных процессов (документы, дедлайны)":"Nejasnosti v administrativních postupech (dokumenty, termíny)",
        "":"",
}

# =============================================================================
# Функция для переноса длинного текста (например, заголовка вопроса) на несколько строк
# =============================================================================
def wrap_text(text, width=60):
    return "\n".join(textwrap.wrap(text, width=width))

# =============================================================================
# Функции для динамического расчёта размеров графиков
# =============================================================================
def calc_figsize_for_vertical_bars(n_bars, max_label_length=0):
    width = max(6, 2 * n_bars)
    height = width / 2.0
    if max_label_length > 15:
        width += (max_label_length - 15) * 0.1
        height = width / 2.5
    return (width, height)

def calc_figsize_for_horizontal_bars(n_bars, max_label_length=0):
    height = max(4, 0.6 * n_bars)
    width = 2.0 * height
    if max_label_length > 30:
        width += (max_label_length - 30) * 0.1
    return (width, height)

def calc_figsize_for_pie(n_slices, max_label_length=0):
    height = max(4, 0.5 * n_slices)
    width = 2.0 * height
    if max_label_length > 20:
        width += (max_label_length - 20) * 0.1
    return (width, height)

# =============================================================================
# Функция для формирования корректного имени файла
# =============================================================================
def sanitize_filename(text):
    sanitized = re.sub(r'[^a-zA-Z0-9_\-]', '_', text)
    return sanitized[:50]

# =============================================================================
# Функция для «умного» разбиения ответа (тип 3)
# =============================================================================
def smart_split(answer):
    """
    Разбивает строку answer на части по запятым, если запятая находится вне скобок
    и за ней следует заглавная буква.
    Если запятая внутри скобок, она не служит разделителем.
    """
    result = []
    current = ""
    paren_level = 0
    i = 0
    while i < len(answer):
        char = answer[i]
        if char == '(':
            paren_level += 1
        elif char == ')':
            if paren_level > 0:
                paren_level -= 1
        if char == ',' and paren_level == 0:
            j = i + 1
            while j < len(answer) and answer[j] == ' ':
                j += 1
            if j < len(answer) and answer[j].isupper():
                result.append(current.strip())
                current = ""
                i += 1
                continue
        current += char
        i += 1
    if current:
        result.append(current.strip())
    return result

# =============================================================================
# Функция для перевода вариантов ответа
# =============================================================================
def translate_answer(answer_text):
    return answer_translations.get(answer_text, answer_text)

# =============================================================================
# Обработка вопросов типа 1 – вертикальный столбчатый график (числовой опрос 1–5)
# =============================================================================
def process_type1(question_text, responses, save_path):
    responses_numeric = pd.to_numeric(responses, errors="coerce").dropna().astype(int)
    total = len(responses_numeric)
    if total == 0:
        print(f"Вопрос «{question_text}»: нет корректных числовых ответов.")
        return

    # Замена текста вопроса согласно словарю
    question_text = question_translations.get(question_text, question_text)

    rating_map = {
        1: "ne",
        2: "spíše ne",
        3: "občas",
        4: "spíše ano",
        5: "ano"
    }
    order = [1, 2, 3, 4, 5]
    counts = {rating: (responses_numeric == rating).sum() for rating in order}
    percentages = {rating: (counts[rating] / total * 100) for rating in order}

    labels = [rating_map[r] for r in order]
    percent_values = [percentages[r] for r in order]

    n_bars = len(labels)
    max_label_length = max(len(lbl) for lbl in labels)
    fig_width, fig_height = calc_figsize_for_vertical_bars(n_bars, max_label_length)

    fig, ax = plt.subplots(figsize=(fig_width, fig_height))
    bars = ax.bar(labels, percent_values, color="skyblue")

    ax.set_xlabel("Míra odpovědi", fontsize=10)
    ax.set_ylabel("Procento odpovědí (%)", fontsize=10)
    wrapped_question = wrap_text(question_text, width=60)
    ax.set_title(f"{wrapped_question}\nCelkem odpovědí: {total}", fontsize=12)

    for bar, pct in zip(bars, percent_values):
        height = bar.get_height()
        ax.annotate(f'{pct:.1f}%',
                    xy=(bar.get_x() + bar.get_width()/2, height),
                    xytext=(0, 3),
                    textcoords="offset points",
                    ha='center', va='bottom', fontsize=9)

    plt.tight_layout()
    filename = os.path.join(save_path, f"graf_type1_{sanitize_filename(question_text)}.png")
    plt.savefig(filename)
    plt.close(fig)
    print(f"Uložen graf typu 1 pro otázku: {question_text}")

# =============================================================================
# Обработка вопросов типа 2 – колáčový graf (короткие текстовые ответы)
# =============================================================================
def process_type2(question_text, responses, save_path):
    responses_clean = responses.dropna().astype(str)
    total = len(responses_clean)
    if total == 0:
        print(f"Вопрос «{question_text}»: отсутствují odpovědi.")
        return

    # Замена текста вопроса
    question_text = question_translations.get(question_text, question_text)
    
    counts = responses_clean.value_counts()
    # При необходимости переводим варианты ответов и суммируем их
    translated_counts = {}
    for key, value in counts.items():
        new_key = answer_translations.get(key, key)
        translated_counts[new_key] = translated_counts.get(new_key, 0) + value

    translated_counts_series = pd.Series(translated_counts)
    percentages = translated_counts_series / total * 100

    n_slices = len(translated_counts_series)
    max_label_length = max(len(str(lbl)) for lbl in translated_counts_series.index)
    fig_width, fig_height = calc_figsize_for_pie(n_slices, max_label_length)

    fig, ax = plt.subplots(figsize=(fig_width, fig_height))
    wedges, texts, autotexts = ax.pie(
        translated_counts_series,
        autopct='%.1f%%',
        startangle=90,
        colors=plt.cm.Paired.colors,
        textprops={'fontsize': 9}
    )
    ax.axis('equal')
    wrapped_question = wrap_text(question_text, width=60)
    ax.set_title(f"{wrapped_question}\nCelkem odpovědí: {total}", fontsize=12)
    ax.legend(
        wedges, translated_counts_series.index,
        title="Odpovědi",
        loc="center left",
        bbox_to_anchor=(1, 0, 0.5, 1),
        fontsize=9
    )

    plt.tight_layout()
    filename = os.path.join(save_path, f"graf_type2_{sanitize_filename(question_text)}.png")
    plt.savefig(filename)
    plt.close(fig)
    print(f"Uložen graf typu 2 pro otázku: {question_text}")

# =============================================================================
# Обработка вопросов типа 3 – horizontální sloupcový graf (длинные ответы)
# =============================================================================
def process_type3(question_text, responses, save_path):
    responses_nonnull = responses.dropna().astype(str)
    if len(responses_nonnull) == 0:
        print(f"Вопрос «{question_text}»: отсутствují odpovědi.")
        return

    # Замена текста вопроса согласно словарю
    question_text = question_translations.get(question_text, question_text)
    
    all_answers = []
    # Для каждого ответа всегда применяем smart_split и добавляем все полученные части
    for resp in responses_nonnull:
        splits = smart_split(resp)
        # Добавляем все части (если smart_split вернула список длины 1 — ок)
        all_answers.extend(splits)
    
    # Теперь для каждого русского варианта выполняем перевод
    all_answers_translated = [answer_translations.get(ans, ans) for ans in all_answers]
    
    if not all_answers_translated:
        print(f"Вопрос «{question_text}»: nepodařilo se rozpoznat varianty odpovědí.")
        return

    counts_series = pd.Series(all_answers_translated).value_counts()
    total_respondents = len(responses_nonnull)
    percentages = (counts_series / total_respondents) * 100

    n_bars = len(counts_series)
    max_label_length = max(len(str(lbl)) for lbl in counts_series.index)
    fig_width, fig_height = calc_figsize_for_horizontal_bars(n_bars, max_label_length)

    fig, ax = plt.subplots(figsize=(fig_width, fig_height))
    bars = ax.barh(counts_series.index, counts_series.values, color="lightgreen")

    ax.set_xlabel("Počet odpovědí", fontsize=10)
    wrapped_question = wrap_text(question_text, width=60)
    ax.set_title(f"{wrapped_question}\nCelkem respondentů: {total_respondents}", fontsize=12)

    max_val = counts_series.values.max()
    for i, bar in enumerate(bars):
        cnt = counts_series.values[i]
        pct = percentages.values[i]
        ax.text(
            cnt + max_val * 0.01,
            bar.get_y() + bar.get_height() / 2,
            f"{int(cnt)} ({pct:.1f}%)",
            va='center', fontsize=9
        )

    plt.tight_layout()
    filename = os.path.join(save_path, f"graf_type3_{sanitize_filename(question_text)}.png")
    plt.savefig(filename)
    plt.close(fig)
    print(f"Uložen graf typu 3 pro otázku: {question_text}")

# =============================================================================
# Определение типа вопроса
# =============================================================================
def detect_question_type(responses):
    """
    Если все ответы можно привести к int и они в {1,2,3,4,5} → тип 1;
    иначе, если хотя бы один ответ содержит символы ',' или ';' → тип 3;
    иначе → тип 2.
    """
    responses_nonnull = responses.dropna()
    try:
        responses_numeric = pd.to_numeric(responses_nonnull, errors="raise")
        unique_vals = set(responses_numeric.unique())
        if unique_vals.issubset({1, 2, 3, 4, 5}):
            return 1
        else:
            return 2
    except Exception:
        if responses_nonnull.astype(str).str.contains(r'[;,]').any():
            return 3
        else:
            return 2

# =============================================================================
# Основная функция
# =============================================================================
def main():
    output_folder = "vystupy_RU"
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    try:
        df = pd.read_excel("RU (Ответы).xlsx")  # Проверьте корректное имя файла
    except Exception as e:
        print("Chyba při načítání Excel souboru:", e)
        return

    for question_text in df.columns:
        if question_text.lower().strip() in ["отметка времени", "timestamp"]:
            continue

        responses = df[question_text]
        q_type = detect_question_type(responses)
        if q_type == 1:
            process_type1(question_text, responses, output_folder)
        elif q_type == 2:
            process_type2(question_text, responses, output_folder)
        elif q_type == 3:
            process_type3(question_text, responses, output_folder)
        else:
            print(f"Nelze určit typ otázky: {question_text}")

if __name__ == '__main__':
    main()
