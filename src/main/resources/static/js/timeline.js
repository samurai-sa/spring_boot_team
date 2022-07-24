function drawTimeline() {
    var spaces = document.getElementsByClassName('spaceName')

    google.charts.load("current", { packages: ["timeline"] });
    google.charts.setOnLoadCallback(drawChart);
    function drawChart() {

        var targetDate = document.getElementById('targetDate');

        var year = targetDate.textContent.substring(0, 4);
        var month = targetDate.textContent.substring(4, 5);
        var date = targetDate.textContent.substring(6, 7);

        var container = document.getElementById('timeline');
        var chart = new google.visualization.Timeline(container);
        var dataTable = new google.visualization.DataTable();
        dataTable.addColumn({ type: 'string', id: 'Room' });
        dataTable.addColumn({ type: 'string', id: 'Name' });
        dataTable.addColumn({ type: 'string', id: 'style', role: 'style' });
        dataTable.addColumn({ type: 'date', id: 'Start' });
        dataTable.addColumn({ type: 'date', id: 'End' });


        // イベントが入ってなくてもタイムラインにスペース生やす
        for (var i = 0; i < spaces.length; i++) {
            dataTable.addRow(
                [spaces[i].textContent, '', 'opacity:0.0', new Date(year, month, date, 10, 0, 0), new Date(year, month, date, 10, 0, 0)]
            );
        }

        if (document.getElementsByClassName("eventName" != null)) {
            var eventNams = document.getElementsByClassName('eventName');
            var spaceNames = document.getElementsByClassName('eventspaceName');
            var starts = document.getElementsByClassName('eventDate'); //開始時間
            var ends = document.getElementsByClassName('eventEndDate'); // 終了時間

            for (var i = 0; i < eventNams.length; i++) {
                var dateStart = starts[i].textContent;
                var dateEnd = ends[i].textContent;

                dataTable.addRow(
                    [spaceNames[i].textContent, eventNams[i].textContent, 'fill-color:#517D99; stroke-color:#FFFFFF; stroke-width:2', new Date(year, month, date, dateStart.substring(8, 10), dateStart.substring(10, 12), 0),
                    new Date(year, month, date, dateEnd.substring(8, 10), dateEnd.substring(10, 12), 0)],
                );
            }
        }

        const matchMedia = window.matchMedia('(max-width: 480px)');
        if (matchMedia.matches) {
            // スマホ
            var options = {
                // グラフ10:00～20:00
                hAxis: {
                    minValue: new Date(year, month, date, 10, 0),
                    maxValue: new Date(year, month, date, 20, 0)
                },
                alternatingRowStyle: false,
                timeline: {
                    rowLabelStyle: { fontName: 'Noto Sans JP', fontSize: 9 },
                    barLabelStyle: { fontName: 'Noto Sans JP', fontSize: 10 }
                }
            }
        } else {
            var options = {
                // グラフ10:00～20:00
                hAxis: {
                    minValue: new Date(year, month, date, 10, 0),
                    maxValue: new Date(year, month, date, 20, 0)
                },
                alternatingRowStyle: false,
                timeline: {
                    rowLabelStyle: { fontName: 'Noto Sans JP', fontSize: 20 },
                    barLabelStyle: { fontName: 'Noto Sans JP', fontSize: 16 }
                }
            };
        }



        chart.draw(dataTable, options);
    }

    // onReSizeイベント    
    window.onresize = function () {
        drawTimeline()
    }
}


function switchStore(year, month) {
    const selectElement = document.querySelector('.select');
    selectElement.addEventListener('change', (event) => {
        const result = event.target.value;
        location.replace('/event/calendar/' + result + '/' + year + '/' + month);
    });

}