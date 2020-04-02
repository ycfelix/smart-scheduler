from flask import request
import pyodbc

def getJSON():
	return request.get_json()

def getCnxn(db_name, port=1433):
	from run import app
	server = app.config['FYPDB_SERVER']
	username = app.config['FYPDB_USERNAME']
	password = app.config['FYPDB_PWD']
	driver = app.config['FYPDB_DRIVER']
	
	return pyodbc.connect('DRIVER='+driver+';SERVER='+server+\
		';PORT=' + str(port) + ';DATABASE='+db_name+';UID='+username+\
		';PWD='+ password)

def getCursor(cursor, cmd):
	return cursor.execute(cmd)