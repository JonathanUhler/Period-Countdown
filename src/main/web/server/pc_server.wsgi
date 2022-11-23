import sys
from flask import Flask


sys.path.insert(0, "/var/www/Period-Countdown-WSGI/server") # Path to the flask server code and HTML/CSS files

from pc_server import run
# Apache WSGI process will look for a flask object called "application", which is set up here
application: Flask = run(transport_ip="localhost", transport_port=9000)
