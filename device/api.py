import urequests

class APIClient:
    def __init__(self, api_url):
        self.url = api_url

    def send_info(self):
        params = {'hello': 'test'}
        r = urequests.get(url=self.url, params=params)

        data = r.json()

        print(data)