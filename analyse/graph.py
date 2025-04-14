import pandas as pd
import matplotlib.pyplot as plt
import os
import re
import textwrap

# -----------------------------------------------------------------------------
# Функция для переноса длинного текста (заголовка) на несколько строк
# -----------------------------------------------------------------------------

def wrap_text(text, width=60):
    return "\n".join(textwrap.wrap(text, width=width))

# -----------------------------------------------------------------------------
# Функции для динамического расчёта размеров графиков
# -----------------------------------------------------------------------------

def calc_figsize_for_vertical_bars(n_bars, max_label_length=0):
    """
    Расчёт размера (width, height) для вертикальной столбчатой диаграммы.
    Отношение сторон ~2:1.
    """
    # Здесь в примере берем ширину как минимум 12, либо 2 дюйма на столбец
    width = max(6, 2 * n_bars)
    height = width / 2.0  # базовое соотношение 2:1

    if max_label_length > 15:
        width += (max_label_length - 15) * 0.1
        height = width / 2.5  # немного скорректируем высоту

    return (width, height)


def calc_figsize_for_horizontal_bars(n_bars, max_label_length=0):
    """
    Расчёт размера (width, height) для горизонтальной столбчатой диаграммы.
    Отношение сторон ~2:1.
    """
    height = max(4, 0.6 * n_bars)
    width = 2.0 * height

    if max_label_length > 30:
        width += (max_label_length - 30) * 0.1

    return (width, height)


def calc_figsize_for_pie(n_slices, max_label_length=0):
    """
    Расчёт размера (width, height) для круговой диаграммы (pie chart).
    Базовое отношение примерно 2.5:1 для обеспечения места под легенду.
    """
    height = max(4, 0.5 * n_slices)
    width = 2.0 * height

    if max_label_length > 20:
        width += (max_label_length - 20) * 0.1

    return (width, height)

# -----------------------------------------------------------------------------
# Функция для формирования корректного имени файла
# -----------------------------------------------------------------------------

def sanitize_filename(text):
    sanitized = re.sub(r'[^a-zA-Z0-9_\-]', '_', text)
    return sanitized[:50]

# -----------------------------------------------------------------------------
# Функция для «умного» разбиения ответа в типе 3
# -----------------------------------------------------------------------------

def smart_split(answer):
    """
    Делит строку answer на части по запятым, если запятая находится вне скобок и за ней идет заглавная буква.
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
            # Найти следующий не пробельный символ
            j = i + 1
            while j < len(answer) and answer[j] == ' ':
                j += 1
            # Если следующий символ существует и является заглавной буквой, считаем, что это разделитель
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

# -----------------------------------------------------------------------------
# Обработка вопросов с оценками от 1 до 5 (Тип 1)
# -----------------------------------------------------------------------------

def process_type1(question_text, responses, save_path):
    """
    Строит вертикальную столбчатую диаграмму для вопросов с ответами 1–5.
    Порядок устанавливается так, что слева – "ne", справа – "ano".
    Заголовок перенесён на новую строку при необходимости.
    """
    responses_numeric = pd.to_numeric(responses, errors="coerce").dropna().astype(int)
    total = len(responses_numeric)
    if total == 0:
        print(f"Вопрос «{question_text}»: нет корректных числовых ответов.")
        return

    # Новый порядок: от 1 до 5
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
    print(f"Сохранён график тип 1 для вопроса: {question_text}")


# -----------------------------------------------------------------------------
# Обработка вопросов с короткими текстовыми ответами (Тип 2) – круговая диаграмма
# -----------------------------------------------------------------------------

def process_type2(question_text, responses, save_path):
    """
    Строит круговую диаграмму для вопросов с текстовыми ответами (не числа 1–5).
    Заголовок переносится, если слишком длинный.
    """
    responses_clean = responses.dropna().astype(str)
    total = len(responses_clean)
    if total == 0:
        print(f"Вопрос «{question_text}»: отсутствují odpovědi.")
        return

    counts = responses_clean.value_counts()
    percentages = counts / total * 100

    n_slices = len(counts)
    max_label_length = max(len(str(lbl)) for lbl in counts.index)
    fig_width, fig_height = calc_figsize_for_pie(n_slices, max_label_length)

    fig, ax = plt.subplots(figsize=(fig_width, fig_height))

    wedges, texts, autotexts = ax.pie(
        counts,
        autopct='%.1f%%',
        startangle=90,
        colors=plt.cm.Paired.colors,
        textprops={'fontsize': 9}
    )
    ax.axis('equal')
    wrapped_question = wrap_text(question_text, width=60)
    ax.set_title(f"{wrapped_question}\nCelkem odpovědí: {total}", fontsize=12)
    ax.legend(
        wedges, counts.index,
        title="Odpovědi",
        loc="center left",
        bbox_to_anchor=(1, 0, 0.5, 1),
        fontsize=9
    )

    plt.tight_layout()
    filename = os.path.join(save_path, f"graf_type2_{sanitize_filename(question_text)}.png")
    plt.savefig(filename)
    plt.close(fig)
    print(f"Сохранён график тип 2 для вопроса: {question_text}")


# -----------------------------------------------------------------------------
# Обработка вопросов с длинными ответами (Тип 3) – горизонтальная столбчатая диаграмма
# -----------------------------------------------------------------------------

def process_type3(question_text, responses, save_path):
    """
    Строит горизонтальную столбчатую диаграмму для вопросов с длинными ответами.
    Каждая ячейка с ответом анализируется функцией smart_split:
      - Если внутри ячейки есть запятая, которая разделяет варианты согласно правилу
        (запятая вне скобок, после неё заглавная буква), то строка разбивается на несколько вариантов.
      - Иначе ячейка считается одним вариантом.
    Заголовок переносится, если слишком длинный.
    """
    responses_nonnull = responses.dropna().astype(str)
    if len(responses_nonnull) == 0:
        print(f"Вопрос «{question_text}»: отсутствují odpovědi.")
        return

    all_answers = []
    for resp in responses_nonnull:
        # Разбиваем ответ по логике: если внутри есть запятая с заглавной буквой после неё вне скобок.
        parts = smart_split(resp)
        # Если разделение дало хотя бы 2 части, используем их, иначе оставляем оригинал.
        if len(parts) > 1:
            all_answers.extend(parts)
        else:
            all_answers.append(resp.strip())

    if not all_answers:
        print(f"Вопрос «{question_text}»: не удалось получить варианты ответов.")
        return

    counts_series = pd.Series(all_answers).value_counts()
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
    print(f"Сохранён график тип 3 для вопроса: {question_text}")

# -----------------------------------------------------------------------------
# Определение типа вопроса
# -----------------------------------------------------------------------------

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

# -----------------------------------------------------------------------------
# Основная функция
# -----------------------------------------------------------------------------

def main():
    output_folder = "vystupy"
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    try:
        df = pd.read_excel("CZ (Ответы).xlsx")  # Проверьте корректность имени файла
    except Exception as e:
        print("Ошибка при загрузке Excel-файла:", e)
        return

    # Пропускаем столбец с меткой времени (если есть)
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
            print(f"Не удалось определить тип для вопроса: {question_text}")

if __name__ == '__main__':
    main()
