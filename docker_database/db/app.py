import os
import json
from flask import Flask, request, jsonify
import redis

app = Flask(__name__)

# Default to the compose redis service if REDIS_URL not provided
r = redis.Redis.from_url(os.environ.get("REDIS_URL"))

# Account storage helpers
def account_key(username: str) -> str:
    return f"accounts:{username.lower()}"


@app.route('/accounts', methods=['GET'])
def list_accounts():
    """Return list of all account JSON objects."""
    keys = r.keys('accounts:*')
    accounts = []
    for k in keys:
        val = r.get(k)
        if val:
            try:
                accounts.append(json.loads(val))
            except Exception:
                # skip malformed
                continue
    return jsonify(accounts)


@app.route('/accounts/<username>', methods=['GET'])
def get_account(username):
    k = account_key(username)
    val = r.get(k)
    if not val:
        return jsonify({'error': 'not found'}), 404
    try:
        return jsonify(json.loads(val))
    except Exception:
        return jsonify({'error': 'stored data is not valid JSON'}), 500

@app.route('/accounts/email/<email>', methods=['GET'])
def get_account_by_email(email):
    """Check if an account exists by email address."""
    # This is not super efficient, but it works.
    # It iterates through all accounts to find a match.
    keys = r.keys('accounts:*')
    for k in keys:
        val = r.get(k)
        if val:
            try:
                account_data = json.loads(val)
                if account_data.get('email', '').lower() == email.lower():
                    # Found a match, return 200 OK
                    return jsonify({'exists': True}), 200
            except Exception:
                continue
    # If the loop finishes without finding the email, return 404 Not Found
    return jsonify({'error': 'not found'}), 404


@app.route('/accounts', methods=['POST'])
def create_account():
    """Create a new account. Body must be JSON and include `username`."""
    data = request.get_json()
    if not data:
        return jsonify({'error': 'invalid json body'}), 400
    username = data.get('username')
    if not username:
        return jsonify({'error': 'missing username'}), 400
    k = account_key(username)
    if r.exists(k):
        return jsonify({'error': 'username already exists'}), 409
    r.set(k, json.dumps(data))
    return jsonify({'result': 'created'}), 201


@app.route('/accounts/<username>', methods=['PUT'])
def update_account(username):
    """Replace or create an account at username with provided JSON body."""
    data = request.get_json()
    if not data:
        return jsonify({'error': 'invalid json body'}), 400
    # Ensure username in URL and body match (if body contains username)
    body_user = data.get('username')
    if body_user and body_user.lower() != username.lower():
        return jsonify({'error': 'username mismatch between URL and body'}), 400
    data['username'] = username
    k = account_key(username)
    r.set(k, json.dumps(data))
    return jsonify({'result': 'updated'}), 200


@app.route('/accounts/<username>', methods=['DELETE'])
def delete_account(username):
    k = account_key(username)
    deleted = r.delete(k)
    if deleted:
        return jsonify({'result': 'deleted'}), 200
    else:
        return jsonify({'error': 'not found'}), 404


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)