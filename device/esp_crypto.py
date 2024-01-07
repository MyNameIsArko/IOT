import uos
from ucryptolib import aes
import ulogging

log = ulogging.getLogger("CRYPTO")


class Encryption:
    def __init__(self, key, iv):
        if isinstance(key, int):
            key = str(key)
        if isinstance(iv, int):
            iv = str(iv)
        if isinstance(key, str):
            key = key.encode()
        if isinstance(iv, str):
            iv = iv.encode()
        self.key = key
        self.iv = iv
        log.info(f"Encryption initialized. Key: {self.key}, IV: {self.iv}")

    def pad(self, data):
        log.info("Padding text")
        return data + b"\x00" * ((16 - (len(data) % 16)) % 16)

    def unpad(self, txt):
        log.info("Unpadding text")
        return txt.strip()

    def encrypt(self, txt):
        data = txt.encode()
        padded = self.pad(data)
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
