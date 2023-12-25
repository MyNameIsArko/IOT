import uos
from ucryptolib import aes

class Encryption :
    def __init__(self, key, iv):
        self.key = key
        self.iv = iv

    def pad(self, txt):
        return txt + " " * (16 - len(txt) % 16)

    def unpad(self, txt):
        return txt.strip()
    
    def encrypt(self, txt):
        padded = self.pad(txt)
        cipher = aes(self.key, 2, self.iv)
        return cipher.encrypt(padded)
    
    def decrypt(self, data):
        decipher = aes(self.key, 2, self.iv)
        padded_txt = decipher.decrypt(data)
        return self.unpad(padded_txt)

