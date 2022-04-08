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
                    redirectUri: this.redirectUri
                },
                methods: {
                    lineLogin() {
                        const url = this.authUrl + '?response_type=code&client_id=' + this.clientId
                            + '&redirect_uri=' + this.redirectUri + '&state=12345&scope=profile',
                            left = (screen.width - 500) / 2,
                            top = (screen.height - 500) / 2;

                        const lineLoginWindow = window.open(url, 'Line Login', 'left=' + left + ',top=' + top + ',height=500,width=500');
                        window.addEventListener('message', (e) => {
                            if (e.data === 'ok') {
                                window.location.href = '/';
                                if (lineLoginWindow) {
                                    lineLoginWindow.close();
                                }
                            }
                        });
                    }
                }
            });
        };
    };
});
