# Рівень 1: Запитайте користувача про його ім'я та виведіть привітання з використанням введеної назви.
def GetName():
    name = input("Введіть ваше ім'я: ")
    print(f"Вітаю вас, {name}!")

# Рівень 2: Напишіть функцію, яка приймає рядок та повертає його обернений варіант. Наприклад, "hello" повинно повернути "olleh".
def ReverseString(text):
    result = ""
    for char in text:
        result = char + result

    is_palindrom = result == text

    if (is_palindrom):
        result = "!" + result + "!"
    return result

# Рівень 3: Створіть функцію, яка приймає список чисел та повертає новий список, який містить лише парні числа.
def GetEven(list):
    even = []
    for num in list:
        if (num % 2 == 0):
            even.append(num)
    return even

# Рівень 4: Напишіть клас "Книга" з властивостями, такими як назва, автор та рік видання. Створіть об'єкт цього класу та виведіть його характеристики.
class Book:
    def __init__(self, title, author, year):
        self.title = title
        self.author = author
        self.year = year

def main():
    GetName()

    text = "lowol"
    print(f"{text}")
    print(f"{ReverseString(text)}")

    list = [1, 2, 3, 4, 5, 6]
    print(f"{list}")
    print(f"{GetEven(list)}")

    book1 = Book("1984", "George Orwell", 1949)
    print(book1.title)
    print(book1.author)
    print(book1.year)

if __name__ == "__main__":
    main()