# api routes
from flask import jsonify, Blueprint, request, abort
from backend.api.utils import getJSON, get_map_similar, get_timetable_similar_user,\
get_checklist_similar_user, get_calendar_similar_user, getCnxn, getCursor, getServerReturnValue, \
resultToListOfDict
import itertools, pyodbc

api = Blueprint('api', __name__)
bad_request_code = 400
# send string request suggestion
# receive -> call function -> give back to them

@api.route('/api/sql_db', methods=['POST'])
def sql_post():
	return_code = 200
	json = getJSON()
	return_dict = {}
	# print('type of json = ' + str(type(json)))
	# print("request = " + str(request.method))
	# print('request.db_name = ' + str(request.values.get('db_name')))
	if not "db_name" in json:
	    return jsonify({'error_msg': 'Database name field does not exist (db_name)'}), bad_request_code
	elif not "sql_cmd" in json:
	    return jsonify({'error_msg': 'SQL command field does not exist (sql_cmd)'}), bad_request_code
	elif json['db_name'] == "":
	    return jsonify({'error_msg': 'Database name is not specified'}), bad_request_code
	elif json['sql_cmd'] == "":
	    return jsonify({'error_msg': 'SQL command is not specified'}), bad_request_code
	else:
		try:
			cnxn = getCnxn(json['db_name'])
			cursor = cnxn.cursor()

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
				numOfResult = -1
				try:
					result = getServerReturnValue(getCursor(cursor, cmd_to_be_exe))
					col_names = [col[0] for col in cursor.description]
					return_dict['result'] = resultToListOfDict(result, col_names)
					numOfResult = len(return_dict['result'])
					return_dict['error_msg'] = None
					
				except Exception as e:
					return_dict['result'] = 'error'
					return_dict['error_msg'] = str(e)

				finally:
					return_dict['num_result'] = numOfResult

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

		except pyodbc.Error as pex:
			return_dict['type'] = "pyodbc error"
			return_dict['result'] = False
			return_dict['error_msg'] = str(pex)
			return jsonify(return_dict)

		except Exception as e:
			abort(500, {'error_msg': 'Please see system message to see what is wrong.', 'system_msg': str(e)})

@api.route('/api/distance_matrix/<int:typ>', methods=['POST'])
def getDistMetric(typ):
	return_code = 200
	json = getJSON()
	return_dict = {}
	return_dict['type'] = ''
	return_dict['result'] = ''
	if not typ in range(0, 4):
		return_dict['type'] = 'invalid type'
		return_dict['result'] = 'Invalid type number. Please see available enums in com.ust.utility.Utils.DistMatrix.'
		return jsonify(return_dict), bad_request_code

	else:
		# print (json)
		# print (json.get('num_user'))
		# print (json.get('user_id'))
		if json.get('user_id') == None or json.get('user_id') == '':
			return_dict['type'] = 'missing parameter(s):'
			return_dict['result'] = 'user_id'

		if json.get('num_user') == None or json.get('num_user') == '':
			if return_dict['type'] == 'missing parameter(s):':
				return_dict['result'] += ', num_user'
			else:
				return_dict['type'] = 'missing parameter(s):'
				return_dict['result'] = 'num_user'

		if return_dict['type'] == 'missing parameter(s):':
			return jsonify(return_dict), bad_request_code

		else:
			try:
				if (typ == 0):
					# MAP
					return_dict['type'] = 'map'
					return_dict['result'] = get_map_similar(json['user_id'], int(json['num_user']))
				elif (typ == 1):
					# TIMETABLE
					return_dict['type'] = 'timetable'
					return_dict['result'] = get_timetable_similar_user(\
					user_id='{id}'.format(id=json['user_id']), n=int(json['num_user']))
				elif (typ == 2):
					# CHECKLIST
					return_dict['type'] = 'checklist'
					return_dict['result'] = get_checklist_similar_user(\
					user_id='{id}'.format(id=json['user_id']), n=int(json['num_user']))
				elif (typ == 3):
					# CALENDAR
					return_dict['type'] = 'calendar'
					return_dict['result'] = get_calendar_similar_user(\
					user_id='{id}'.format(id=json['user_id']), n=int(json['num_user']))
				return jsonify(return_dict), return_code

			except ValueError as ve:
				abort(500, {'error_msg': 'Please check if there are non-numeric characters in num_user', 'system_msg': str(ve)})
