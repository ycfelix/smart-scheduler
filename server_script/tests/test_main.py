class TestMainClass:
	def test_home_page(self, client):
	    rv = client.get('/')
	    assert rv.status_code == 200
	    assert rv.data == b'Smart Scheduler server'

	def test_help_page(self, client):
	    rv = client.get('/help')
	    assert rv.status_code == 200