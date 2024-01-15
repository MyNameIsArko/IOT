import uos
from ucryptolib import aes
import ulogging
import ubinascii

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
        block_size = 16
        padding_needed = block_size - len(data) % block_size
        return data + bytes([padding_needed] * padding_needed)
        # return data + b"\x00" * ((16 - (len(data) % 16)) % 16)

    def unpad(self, txt):
        log.info("Unpadding text")
        return txt.strip()

    def encrypt(self, txt):
        data = txt.encode()
        padded = self.pad(data)
        log.info("Creating cipher")
        cipher = aes(self.key, 2, self.iv)
        log.info("Encrypting data")
        encrypted = cipher.encrypt(padded)
        return ubinascii.b2a_base64(encrypted)

    def decrypt(self, data):
        log.info("Creating cipher")
        decipher = aes(self.key, 2, self.iv)
        log.info("Decrypting data")
        padded_txt = decipher.decrypt(data)
        return self.unpad(padded_txt)
