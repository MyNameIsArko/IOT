import urequests

class APIClient:
    def __init__(self):
        # api/Device/register
        # do authorization wkładam token
        # w body przesyłam {'Mac': mac}
        # kod 200 jak dodane, kod 401 jak token zły, kod 400 jak coś po stronie serwa
        self.url = 'http://srv3.enteam.pl:180/api/Device/register'

    def send_info(self, token, user_id, mac):
        headers = {
            "Authorization" : "Bearer " + token
        }
        json = {'Mac': mac, 'UserId': user_id}
        r = urequests.post(url=self.url, headers=headers, json=json)

        if r.status_code == 200:
            print('All good')
        else:
            print(r.text)