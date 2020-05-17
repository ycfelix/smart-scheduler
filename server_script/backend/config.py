# app config
# import os

# class Config:
# 	SECRET_KEY = os.environ.get('FYPSERVER_SECRET_KEY')
# 	JSON_SORT_KEYS = False
# 	DEBUG = True
# 	FYPDB_SERVER = os.environ.get('FYPDB_SERVER')
# 	FYPDB_USERNAME = os.environ.get('FYPDB_USERNAME')
# 	FYPDB_PWD = os.environ.get('FYPDB_PWD')
# 	FYPDB_DRIVER = os.environ.get('FYPDB_DRIVER')

import json

with open('/etc/server_config.json') as config_file:
	config = json.load(config_file)

class Config:
	SECRET_KEY = config.get('FYPSERVER_SECRET_KEY')
	JSON_SORT_KEYS = False
	FYPDB_SERVER = config.get('FYPDB_SERVER')
	FYPDB_USERNAME = config.get('FYPDB_USERNAME')
	FYPDB_PWD = config.get('FYPDB_PWD')
	FYPDB_DRIVER = config.get('FYPDB_DRIVER')

