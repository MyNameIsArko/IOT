import urequests
import ulogging
import ujson

log = ulogging.getLogger("REQUEST")


class APIClient:
    def __init__(self):
        self.regiser_url = "https://srv9cf.enteam.pl:180/api/Device/register"
        self.exist_url = "https://srv9cf.enteam.pl:180/api/Device/exists"

    def send_info(self, token, user_id, mac):
        headers = {"Authorization": "Bearer " + token}
        json = {"Mac": mac, "UserId": user_id}
        log.info("Sending request to server")
        r = urequests.post(url=self.regiser_url, headers=headers, json=json)

        if r.status_code == 200:
            log.info("Device registered")
        else:
            log.error(r.text)

    def check_if_exists(self, mac):
        json = {"mac": mac}
        r = urequests.post(url=self.exist_url, json=json)
        if r.status_code == 200:
            log.info("Device exists")
            return True
        elif r.status_code == 404:
            log.info("Device does not exist")
            return False
        else:
            log.waring("Error from server: " + r.text)
            return False
