$(function () {
    window.App = function (authUrl, clientId, redirectUri) {
        this.authUrl = authUrl;
        this.clientId = clientId;
        this.redirectUri = redirectUri;

        this.init = function () {
            return new Vue({
                el: '#app',
                data: {
                    authUrl: this.authUrl,
                    clientId: this.clientId,
                    redirectUri: this.redirectUri,
                    message: '',
                    notifyStatus: ''
                },
                methods: {
                    lineNotify() {
                        const url = this.authUrl + '?response_type=code&client_id=' + this.clientId
                            + '&redirect_uri=' + this.redirectUri + '&state=12345&scope=notify',
                            left = (screen.width - 500) / 2,
                            top = (screen.height - 500) / 2;

                        const lineNotifyWindow = window.open(url, 'Line Notify', 'left=' + left + ',top=' + top + ',height=400,width=800');
                        window.addEventListener('message', (e) => {
                            if (e.data === 'ok') {
                                window.location.reload();
                                if (lineNotifyWindow) {
                                    lineNotifyWindow.close();
                                }
                            }
                        });
                    },
                    sendNotify() {
                        this.notifyStatus = '';
                        this.$http.post('/oauth2/notify', this.message).then((res) => {
                            if (res.body && res.body.message === 'ok') {
                                this.notifyStatus = '發送成功';
                            } else {
                                this.notifyStatus = '發送失敗';
                            }
                        });
                    }
                }
            });
        };
    };
});
