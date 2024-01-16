import uos
from ucryptolib import aes
import ulogging

log = ulogging.getLogger('CRYPTO')

class Encryption :
    def __init__(self, key, iv):
        if isinstance(key, str):
            key = key.encode()
        if isinstance(iv, str):
            iv = iv.encode()
        self.key = key
        self.iv = iv
        self.wifi_key = b'1811035398261360'
        self.wifi_iv = b'8197555279945598'

    def pad(self, data):
        log.info("Padding text")
        block_size = 16
        padding_needed = block_size - len(data) % block_size
        return data + bytes([padding_needed] * padding_needed)

    def unpad(self, data):
        log.info("Unpadding text")
        return data[:-ord(data[-1:])]
    
    def encrypt(self, txt):
        padded = self.pad(txt)
        log.info("Creating cipher")
        cipher = aes(self.key, 2, self.iv)
        log.info("Encrypting data")
        return cipher.encrypt(padded)
    
    def decrypt(self, data):
        log.info("Creating cipher")
        decipher = aes(self.wifi_key, 2, self.wifi_iv)
        log.info("Decrypting data")
        padded_txt = decipher.decrypt(data)
        return self.unpad(padded_txt)

