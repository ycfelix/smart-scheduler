from backend import create_app
import pytest

@pytest.fixture(scope='class')
def app():
    app = create_app(test_config={'TESTING': True,
    	'DEBUG': True})
    return app

@pytest.fixture(scope='class')
def client(app):
    """A test client for the app."""
    return app.test_client()