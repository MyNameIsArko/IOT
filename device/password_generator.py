import random

def generate_password(length):
    characters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ' + '0123456789' + '!@#$%^&*'
    password = ''.join(random.choice(characters) for _ in range(length))
    return password