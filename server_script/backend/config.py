import os

class Config:
	SECRET_KEY = os.environ.get('FYPSERVER_SECRET_KEY')
	JSON_SORT_KEYS = False
	DEBUG = True
	FYPDB_SERVER = os.environ.get('FYPDB_SERVER')
	FYPDB_USERNAME = os.environ.get('FYPDB_USERNAME')
	FYPDB_PWD = os.environ.get('FYPDB_PWD')
	FYPDB_DRIVER = os.environ.get('FYPDB_DRIVER')