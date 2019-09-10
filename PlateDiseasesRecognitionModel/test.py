import requests
import json
import base64

with open('Gray-leaf-spot-Adam-Sisson-3-copy.jpg', 'rb') as imgFile:
    image = base64.b64encode(imgFile.read())

scoring_uri = 'http://b39317c0-eb05-4081-bd3e-919e07d9fc14.eastus.azurecontainer.io/score'
headers = {'Content-Type':'application/json'}

#test_data = json.dumps({'img': image})

response = requests.post(scoring_uri, data=image, headers=headers)
print(response.status_code)
print(response.elapsed)
print(response.json())