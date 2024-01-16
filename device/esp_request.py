import urequests
import ulogging
import ujson

log = ulogging.getLogger("REQUEST")

class APIClient:
    def __init__(self):
        self.url = 'https://srv9cf.enteam.pl/api/Device/register'
        self.check_url = 'https://srv9cf.enteam.pl/api/Device/exists'


    def send_info(self, token, user_id, mac):
        headers = {
            "Authorization" : "Bearer " + token
        }
        json = {'Mac': mac, 'UserId': user_id}
        log.info('Sending request to server')
        r = urequests.post(url=self.url, headers=headers, json=json)

        if r.status_code == 200:
            log.info('Device registered')
        else:
            log.error(r.text)
        r.close()

    def check_if_exists(self, mac):
        json = {"mac": mac}
        try:
            r = urequests.post(url=self.exist_url, json=json)
            if r.status_code == 200:
                log.info("Device exists")
                r.close()
                return True
            elif r.status_code == 404:
                log.warning("Device does not exist")
                r.close()
                return False
            else:
                log.waring("Unknown code from server: " + r.text)
                r.close()
                return False
        except OSError:
            log.error("Error while sending 'if exists' request")
            r.close()
            return True  # We return true as we don't want to reset device when server is down or wifi not connected


