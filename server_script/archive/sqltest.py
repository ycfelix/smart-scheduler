from flask import Flask, jsonify, request, flash, \
	redirect, render_template, url_for
import pyodbc, json

app = Flask(__name__)

server = 'smartphdbserver.database.windows.net'
database = 'TestData'
username = 'smartphadmin'
password = 'reallysmartAh!0'
driver = '{ODBC Driver 17 for SQL Server}'
cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1433;DATABASE='+database+';UID='+username+';PWD='+ password)
cursor = cnxn.cursor()
# cursor.execute("SELECT * FROM [Products]")

# row = cursor.fetchone()
# print ("ID" + "\t" + "Producy Name")
# print ("--------------------------")
# while row:
#     print (str(row[0]) + "\t" + str(row[1]))
#     row = cursor.fetchone()

# s = "<table style='border:1px solid black'>"
# s = s + "<tr style='border:1px solid black'>\
# <th style='border:1px solid black'>ID</th>\
# <th style='border:1px solid black'>Product Name</th>\
# <th style='border:1px solid black'>Price</th>\
# <th style='border:1px solid black'>Product Description</th></tr>"
# for row in cursor:
# 	s = s + "<tr style='border:1px solid black'>"
# 	for x in row:  
# 		s = s + "<td style='border:1px solid black'>" + str(x) + "</td>"  
# 	s = s + "</tr>"
# s = s + "</table>"

@app.route('/')  
@app.route('/index')
def index():
	return "<html><body><h1>It works!</h1></body></html>" 

@app.route('/records')
def queryAll():
	cursor.execute("SELECT * FROM Products")
	s = "<table style='border:1px solid black'>"
	s = s + "<tr style='border:1px solid black'>\
	<th style='border:1px solid black'>ID</th>\
	<th style='border:1px solid black'>Product Name</th>\
	<th style='border:1px solid black'>Price</th>\
	<th style='border:1px solid black'>Product Description</th></tr>"
	for row in cursor:
		s = s + "<tr style='border:1px solid black'>"
		for x in row:  
			s = s + "<td style='border:1px solid black'>" + str(x) + "</td>"  
		s = s + "</tr>"
	s = s + "</table>"
	return "<html><body><h1>All records</h1>" + s + "</body></html>"

@app.route('/records/<int:id>', methods=['GET'])
def queryWithID(id):
	result = cursor.execute("SELECT * FROM Products WHERE ProductID = ?", str(id))
	s = "<table style='border:1px solid red'>"
	s = s + "<tr style='border:1px solid red'>\
	<th style='border:1px solid red'>ID</th>\
	<th style='border:1px solid red'>Product Name</th>\
	<th style='border:1px solid red'>Price</th>\
	<th style='border:1px solid red'>Product Description</th></tr>"
	for row in result:
		s = s + "<tr style='border:1px solid red'>"
		for x in row:  
			s = s + "<td style='border:1px solid red'>" + str(x) + "</td>"  
		s = s + "</tr>"
	s = s + "</table>"
	return "<html><body><h1>Query result</h1>" + s + "</body></html>"

@app.route('/records/<string:id>', methods=['DELETE'])
def deleteQueryWithID(id):
	cursor.execute('''
		DELETE FROM Products
		WHERE ProductID = ?
		''', str(id))
	cursor.commit()
	return redirect(url_for('queryAll'))

if __name__ == '__main__':
	app.run(debug=True)
