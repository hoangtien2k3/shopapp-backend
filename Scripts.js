const baseUrl = pm.environment.get("API_PREFIX");

const postRequest = {
    url: baseUrl + "users/login",
    method: 'POST',
    header: {
        'Content-Type': 'application/json',
    },
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            "phone_number" : "0828007854",
            "password" : "123456789"
        })
    }
};

pm.sendRequest(postRequest, (error, response) => {
    if (error) {
        console.error(error);
    } else {
        if (response.code === 200) {
            const token = JSON.parse(response.text()).payload.token;
            pm.environment.set("TOKEN", token);
            console.log(token)
        } else {
            console.error("Unexpected response code:", response.code);
        }
    }
});