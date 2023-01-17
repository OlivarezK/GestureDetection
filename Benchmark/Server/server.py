from flask import Flask
from flask import request
from datetime import datetime
import os
import json

app = Flask(__name__)


@app.route('/')
def test_api():
    return 'API is working normally'


@app.route('/benchmark', methods=["POST"])
def post_gesture():
    benchmark_results = request.json
    print(benchmark_results)
    save_report(benchmark_results)

    return ("Benchmark recorded successfully!")


def save_report(benchmark_results):
    save_path = '.\\reports'
    timestamp = datetime.now().strftime("%Y_%m_%d_%H_%M_%S")
    file_name = f"benchmark_{timestamp}.txt"
    file_path = os.path.join(save_path, file_name)
    file_content = json.dumps(benchmark_results, indent=2)
    with open(file_path, "w") as f:
        f.write(file_content)
