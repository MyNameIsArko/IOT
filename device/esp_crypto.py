import uos
from ucryptolib import aes
import ulogging
import ubinascii

log = ulogging.getLogger("CRYPTO")


class Encryption:
    def __init__(self, key, iv):
        if isinstance(key, str):
            key = key.encode()
        if isinstance(iv, str):
            iv = iv.encode()
        self.key = key
        self.iv = iv

    def pad(self, data):
        log.info("Padding text")
        block_size = 16
        padding_needed = block_size - len(data) % block_size
        return data + bytes([padding_needed] * padding_needed)

    def to_base64(self, data):
        log.info("Encoding to base64")
        return ubinascii.b2a_base64(data)

    def encrypt(self, txt):
        padded = self.pad(txt)
        log.info("Creating cipher")
        cipher = aes(self.key, 2, self.iv)
        log.info("Encrypting data")
        encrypted_txt = cipher.encrypt(padded)
        return self.to_base64(encrypted_txt)


class Decryption:
    def __init__(self):
        self.key = b"1811035398261360"
        self.iv = b"8197555279945598"

    def unpad(self, data):
        log.info("Unpadding text")
        return data[: -ord(data[-1:])]

    def from_base64(self, data):
        log.info("Decoding base64")
        return ubinascii.a2b_base64(data)

    def decrypt(self, data):
        log.info("Creating cipher")
        decipher = aes(self.key, 2, self.iv)
        decrypted_data = self.from_base64(data)
        log.info("Decrypting data")
        padded_txt = decipher.decrypt(decrypted_data)
        byte_text = self.unpad(padded_txt)
        return byte_text.decode("utf-8")
