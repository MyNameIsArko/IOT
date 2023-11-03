import urequests

class APIClient:
    def __init__(self):
        # api/Device/register
        # do authorization wkładam token
        # w body przesyłam {'Mac': mac}
        # kod 200 jak dodane, kod 401 jak token zły, kod 400 jak coś po stronie serwa
        self.url = 'TO GET/api/Device/register'

    def send_info(self, mac, jwt_token):
        header = {
            'Bearer': jwt_token
        }
        params = {'Mac': mac}
        r = urequests.get(url=self.url, header=header, params=params)

        if r.status_code == 200:
            print('All good')
        elif r.status_code == 400:
            print('Bad jwt token')
        else:
            print('Error on server part')