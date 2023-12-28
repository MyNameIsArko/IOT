import uos
from ucryptolib import aes
import ulogging

log = ulogging.getLogger('CRYPTO')

class Encryption :
    def __init__(self, key, iv):
        self.key = key
        self.iv = iv

    def pad(self, txt):
        log.info("Padding text")
        return txt + " " * (16 - len(txt) % 16)

    def unpad(self, txt):
        log.info("Unpadding text")
        return txt.strip()
    
    def encrypt(self, txt):
        padded = self.pad(txt)
        log.info("Creating cipher")
        cipher = aes(self.key, 2, self.iv)
        log.info("Encrypting data")
        return cipher.encrypt(padded)
    
    def decrypt(self, data):
        log.info("Creating cipher")
        decipher = aes(self.key, 2, self.iv)
        log.info("Decrypting data")
        padded_txt = decipher.decrypt(data)
        return self.unpad(padded_txt)

