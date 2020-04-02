from flask import Flask, jsonify, request, \
	redirect, url_for, render_template
import pyodbc, json, os, itertools

app = Flask(__name__)
app.config['JSON_SORT_KEYS'] = False
app.config['SECRET_KEY'] = os.environ.get('FYPSERVER_SECRET_KEY')

server = os.environ.get('FYPDB_SERVER')
username = os.environ.get('FYPDB_USERNAME')
password = os.environ.get('FYPDB_PWD')
driver = os.environ.get('FYPDB_DRIVER')

def getJSON():
	return request.get_json()

def getCnxn(db_name, port=1433):
	return pyodbc.connect('DRIVER='+driver+';SERVER='+server+\
		';PORT=' + str(port) + ';DATABASE='+db_name+';UID='+username+\
		';PWD='+ password)

def getCursor(cursor, cmd):
	return cursor.execute(cmd)

@app.route('/')
def index():
	return "Smart Scheduler server"

@app.route('/help')
def help():
	return render_template('manual.html')

@app.route('/api/user/logout', methods=['GET'])
def logout():
	json = getJSON()
	pass

@app.route('/api/user/login', methods=['POST'])
def login():
	json = getJSON();
	if not "email" in json:
		return jsonify({'error': 'No email'})
	elif not "hashed_pwd" in json:
		return jsonify({'error': 'No password'})
	elif "email" == "":
	    return jsonify({'error': 'Email is not specified'})
	elif "hashed_pwd" == "":
	    return jsonify({'error': 'Password is not specified'})
	else:
		try:
			cnxn = getCnxn('Smart Scheduler')
			cursor = cnxn.cursor()

		except pyodbc.Error as pex:
			return jsonify({'error': 500, 'error msg': str(pex)})

		else:
			return_dict = {}
			str = "select Email, HashedPwd, LogIn from dbo.Accounts where Email='%s'" % json['email']
			check = getCursor(cursor, str).fetchall()
			if len(check) == 1:
				# password checking can be improved -> hashing
				if (json['hashed_pwd'] == check[0][1]):
					if (check[0][2] == 0):
						getCursor(cursor, "update dbo.Accounts set LogIn=1 where Email='%s'" % json['email'])
						return_dict['result'] = True
						return_dict['error_code'] = -1 # no error
					else:
						return_dict['result'] = False
						return_dict['error_code'] = 0 # already logged in
				else:
					return_dict['result'] = False
					return_dict['error_code'] = 1 # wrong password

			else:
				return_dict['result'] = False
				if len(check) == 0:
					return_dict['error_code'] = 2 # email not registered
				elif len(check) > 1:
					return_dict['error_code'] = 3 # need debug: check if duplicated email exists in database.
				else:
					return_dict['error_code'] = 4 # server error

		cnxn.commit()
		cursor.close()
		cnxn.close()
		return jsonify(return_dict)

@app.route('/api/sql_db', methods=['POST'])
def sql_post():
	json = getJSON()
	if not "db_name" in json:
	    return jsonify({'error': 'Database name field does not exist (db_name)'})
	elif not "sql_cmd" in json:
	    return jsonify({'error': 'SQL command field does not exist (sql_cmd)'})
	elif "db_name" == "":
	    return jsonify({'error': 'Database name is not specified'})
	elif "sql_cmd" == "":
	    return jsonify({'error': 'SQL command is not specified'})
	else:
		return_dict = {}
		try:
			cnxn = getCnxn(json['db_name'])
			cursor = cnxn.cursor()

		except pyodbc.Error as pex:
			return_dict['type'] = "pyodbc error"
			return_dict['result'] = False
			return_dict['error msg'] = str(pex)

		else:
			listOfSupportedOp = ['SELECT', 'UPDATE', 'INSERT', 'DELETE', 'IF', 'CREATE', 'DROP']
			cmd_to_be_exe = json['sql_cmd']
			split_items = cmd_to_be_exe.split()
			cmd_type = split_items[0]
			tb_name = None

			for i in range(1, len(split_items)):
				if split_items[i-1].upper() in ['FROM', 'INTO', 'DELETE', 'UPDATE'] and split_items[i].upper() != 'SELECT':
					tb_name = split_items[i]
					break

			if (cmd_type.upper() == 'SELECT'):
				return_dict['type'] = 'query'
				try:
					result = getCursor(cursor, cmd_to_be_exe).fetchall()
					col_names = [col[0] for col in cursor.description]
					return_dict['type'] = 'query'
					temp_list = []

					for row in result:
						temp = {}
						for (key, value) in itertools.zip_longest(col_names, row):
							if (type(value).__name__ == "Decimal"):
								temp[str(key)] = float(round(value, 2))
							else:
								temp[str(key)] = value
						temp_list.append(temp)

					return_dict['result'] = temp_list
				except Exception as e:
					return_dict['result'] = 'error'
					return_dict['error msg'] = str(e)

			elif (cmd_type.upper() != 'SELECT' and \
				cmd_type.upper() in listOfSupportedOp):
				num_rows_affected = 0 # Assume no row is affected
				try:
					return_dict['type'] = cmd_type.upper() if cmd_type.upper() != listOfSupportedOp[4] else 'IF ... ELSE ...'
					getCursor(cursor, cmd_to_be_exe)
					num_rows_affected = cursor.rowcount
					return_dict['result'] = True

				except Exception as e:
					return_dict['result'] = False
					return_dict['error msg'] = str(e)

				finally:
					return_dict['num_rows_affected'] = num_rows_affected if num_rows_affected != -1 else 'not applicable'

			else:
				return_dict['type'] = 'unsupported'
				return_dict['result'] = False
				return_dict['error msg'] = 'unsupported operation'

			cnxn.commit()
			cursor.close()
			cnxn.close()

		return jsonify(return_dict)

# running web app in server
if __name__ == '__main__':
	app.run(debug=True)
