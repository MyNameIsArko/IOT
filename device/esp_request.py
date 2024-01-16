import urequests
import ulogging
import ujson

log = ulogging.getLogger("REQUEST")


class APIClient:
    def __init__(self):
        self.regiser_url = "https://srv9cf.enteam.pl:443/api/Device/register"
        self.exist_url = "https://srv9cf.enteam.pl:443/api/Device/exists"

    def send_info(self, token, user_id, mac):
        headers = {"Authorization": "Bearer " + token}
        json = {"Mac": mac, "UserId": user_id}
        log.info("Sending request to server")
        try:
            r = urequests.post(url=self.regiser_url, headers=headers, json=json)

            if r.status_code == 200:
                log.info("Device registered")
                return True
            else:
                log.warning(r.text)
                return False
        except OSError:
            log.error("Error while sending 'register' request")
            return False

    def check_if_exists(self, mac):
        json = {"mac": mac}
        try:
            r = urequests.post(url=self.exist_url, json=json)
            if r.status_code == 200:
                log.info("Device exists")
                return True
            elif r.status_code == 404:
                log.warning("Device does not exist")
                return False
            else:
                log.waring("Unknown code from server: " + r.text)
                return True  # We return true as we don't want to reset device when server is down
        except OSError:
            log.error("Error while sending 'if exists' request")
            return False
