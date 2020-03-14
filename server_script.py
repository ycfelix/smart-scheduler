from flask import Flask, jsonify, request, flash, \
	redirect, render_template, url_for
import pyodbc, json

app = Flask(__name__)

server = 'smartphdbserver.database.windows.net'
database = 'Smart Scheduler'
username = 'smartphadmin'
password = 'reallysmartAh!0'
driver = '{ODBC Driver 17 for SQL Server}'
cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1433;DATABASE='+database+';UID='+username+';PWD='+ password)
cursor = cnxn.cursor()

app = Flask(__name__)

# root
@app.route('/')
def index():
	"""
	this is a roo tfir of my server
	:return: str
	"""
	return "This is root!"

# GET
@app.route('/users/<user>')
def hello_user(user):
	"""
	this serves as a demo purpose
	:param user:
	:return: str
	"""
	return "Hello %s!" % user

# POST
@app.route('/api/post_some_data', methods=['POST'])
def get_text_prediction():
	"""
	predicts requested text whether it is ham or spam
	:return: json
	"""
	json = request.get_json()
        # print(json)
	if len(json['text']) == 0:
		return jsonify({'error': 'invalid input'})

	return jsonify({'you sent this': json['text']})

#Query with SQL cmd
@app.route('/api/query/<string:cmd>', methods=['GET'])
def sql_query(cmd):
    try:
        cursor.execute(cmd)
        s = "<p style='font-size: 2em'>Query result:</p>"
        for row in cursor:
            s = s + "<hr>"
            for x in row:
                s = s + str(x) + "&emsp;"
    
        return s
    except:
        return "invalid SQL command!"
    # return "Under construction " + cmd

# running web app in server
if __name__ == '__main__':
	app.run(debug=True)
