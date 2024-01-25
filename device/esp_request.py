import urequests
import ulogging
import ujson

log = ulogging.getLogger("REQUEST")


class APIClient:
    def __init__(self):
        self.url = "https://srv9cf.enteam.pl/api/Device/register"
        self.check_url = "https://srv9cf.enteam.pl/api/Device/exists"

    def send_info(self, token, user_id, mac):
        headers = {"Authorization": "Bearer " + token}
        json = {"Mac": mac, "UserId": user_id}
        log.info("Sending request to server")
        r = urequests.post(url=self.url, headers=headers, json=json)

        if r.status_code == 200:
            log.info("Device registered")
        else:
            log.error(r.text)
        r.close()
