# api routes
from flask import jsonify, Blueprint
from backend.api.utils import *
import itertools, pyodbc

api = Blueprint('api', __name__)

@api.route('/api/distance_metric', methods=['GET'])
def getDistMetric():
	pass

@api.route('/api/sql_db', methods=['POST'])
def sql_post(input_json=None):
	# if input_json == None:
	# 	json = getJSON()
	# else:
	# 	json = input_json
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
			return_dict['error_msg'] = str(pex)

		else:
			listOfSupportedOp = ['SELECT', 'UPDATE', 'INSERT', 'DELETE', 'IF', 'CREATE', 'DROP', 'ALTER']
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
					return_dict['error_msg'] = str(e)

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
					return_dict['error_msg'] = str(e)

				finally:
					return_dict['num_rows_affected'] = num_rows_affected if num_rows_affected != -1 else 'not applicable'

			else:
				return_dict['type'] = 'unsupported'
				return_dict['result'] = False
				return_dict['error_msg'] = 'unsupported operation'

			cnxn.commit()
			cursor.close()
			cnxn.close()

		return jsonify(return_dict)
