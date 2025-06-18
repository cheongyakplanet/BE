$(document).ready(function () {
    const auth = getToken();
    if (auth === '') {
        $('#login-true').hide();
        $('#login-false').show();
    } else {
        $('#login-true').show();
        $('#login-false').hide();
    }
});

let host = 'http://' + window.location.host;

function logout() {
    //     토큰    삭제

    Cookies.remove('Authorization', {path: '/'});
    window.location.href = host + '/api/user/login-page';
}

function getToken() {

    let auth = Cookies.get('Authorization');

    if (auth === undefined) {
        return '';
    }

    // kakao     로그인    사용한    경우 Bearer    추가

    if (auth.indexOf('Bearer') === -1 && auth !== '') {
        auth = 'Bearer ' + auth;
    }

    return auth;
}