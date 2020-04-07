from flask import request
from backend.config import config
import pyodbc
# import os

def getJSON():
	return request.get_json()

def getCnxn(db_name, port=1433):
	# server = os.environ.get['FYPDB_SERVER']
	# username = os.environ.get['FYPDB_USERNAME']
	# password = os.environ.get['FYPDB_PWD']
	# driver = os.environ.get['FYPDB_DRIVER']
	server = config.get('FYPDB_SERVER')
	username = config.get('FYPDB_USERNAME')
	password = config.get('FYPDB_PWD')
	driver = config.get('FYPDB_DRIVER')
	
	return pyodbc.connect('DRIVER='+driver+';SERVER='+server+\
		';PORT=' + str(port) + ';DATABASE='+db_name+';UID='+username+\
		';PWD='+ password)

def getCursor(cursor, cmd):
	return cursor.execute(cmd)