from flask import Flask
from flask import request
app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'This is my first API call!'

@app.route('/post', methods=["POST"])
def hello_post():
    value = request.form['value']
    fileNum = request.form['fileNum']
    
    writeFile(value, fileNum)
    
    return("Gesture recorded successfully!")

def writeFile(data, fNum):
    save_path = 'C:/Users/ADMIN/flask-test/Scripts/gesture_files/'
    gesture = 'gesture_'
    
    f = open(save_path + gesture + fNum + ".txt","w+")
    f.write(data)
    f.close()
