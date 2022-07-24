// 印刷イベント用

function printClick() {
    window.print();

    Swal.fire({
        html: '印刷を終了します。<br>よろしいですか？',
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: '終了する',
        denyButtonText: 'いいえ',
        cancelButtonText: '整理券をキャンセル'
    }).then((result) => {
        if (result.isConfirmed) {
            // 印刷終了、一覧へ遷移
            const slackForm = document.forms["ticketRegister"];
            slackForm.method = "POST";
            slackForm.submit();
            return true;
        } else if (result.isDenied) {
            // 印刷ループ
            printClick();
        } else {
            location.replace("/event/");
        }
    })
}

//イベントリスト一覧
function eventListChange(switchForm){
	var form = document.forms[switchForm];
	form.method = "POST";	 
	form.submit();
}

