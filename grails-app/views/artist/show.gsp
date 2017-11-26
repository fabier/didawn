<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
    <script>
        $(function () {
            var audioElements = $("audio");
            audioElements.on('ended', function () {
                var sm = $(this).parent().find("small");
                sm.text("▶");
            });
            audioElements.on('playing', function () {
                var sm = $(this).parent().find("small");
                sm.text("⏸");
            });
            audioElements.on('pause', function () {
                var sm = $(this).parent().find("small");
                sm.text("▶");
            });
        });

        function playPause(id) {
            var player = $("#audio-" + id);
            var btn = $("#btn-" + id);
            if (player.prop("paused") === true) {
                var players = $("audio");
                var playing = $.grep(players, function (element) {
                    var isPaused = $(element).prop("paused")
                    return isPaused === false;
                }).map(function (element) {
                    $(element).trigger("pause");
                });
                player.trigger("play");
            } else {
                player.trigger("pause");
            }
        }

        function downloadAll() {
            var rows = $("tbody input[type=checkbox]:checked");
            downloadRow(rows, 0);
        }

        function downloadRow(rows, index) {
            if (rows && rows.length > index) {
                var row = rows[index];
                var tr = $(row).parent().parent();
                var artist = tr.data("artist");
                var title = tr.data("title");
                var url = '${raw(createLink(controller: "playlist", action: "ajaxTrack",
                         params: [artist:"_ARTIST_", title:"_TITLE_"]))}'
                        .replace("_ARTIST_", artist)
                        .replace("_TITLE_", title);
                $.ajax(url).done(function (data) {
                    var id = data.id;
                    var dataParam = data.data;
                    if (id !== undefined && data !== undefined) {
                        var dataUrl = '${createLink([controller: "search", action: "dl", id: "_ID_", params: [data: "_DATA_"]])}'
                                .replace('_ID_', id)
                                .replace('_DATA_', dataParam);
                        downloadAsync(dataUrl, artist + " - " + title + ".mp3", function () {
                            downloadRow(rows, index + 1);
                        });
                    } else {
                        downloadRow(rows, index + 1);
                    }
                });
            }
        }

        function toggleSelectAll(element) {
            var e = $(element);
            var checked = e.prop("checked");
            var innerTable = e.closest("table");
            innerTable.find("input[type=checkbox]").prop("checked", checked);
        }
    </script>
</head>

<body>
<div class="container">
    <g:each in="${albums}" var="album">
        <g:set var="artistName" value="${artist.name}"/>
        <g:set var="albumTitle" value="${album.title}"/>
        <div class="row">
            <div class="col-sm-3 text-center">
                <h1>${album.title}</h1>
                <img src="${album.coverBig}" class="full-width-image"/>

                <p>

                <div class="btn btn-primary" onclick="downloadAll();">
                    <i class="glyphicon glyphicon-download-alt"></i>
                    Download All
                </div>
            </p>
            </div>

            <div class="col-sm-9">
                <table class="table small">
                    <thead>
                    <tr>
                        <th class="col-sm-1">
                            <input id="select-all" type="checkbox" onclick="toggleSelectAll(this);">
                        </th>
                        <th class="col-sm-1">#</th>
                        <th class="col-sm-4">Album</th>
                        <th class="col-sm-4">Title</th>
                        <th class="col-sm-3">Play</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${albumAndTracks.get(album)}" var="track">
                        <g:set var="title" value="${track.title}"/>
                        <tr data-artist="${artist.name}" data-title="${title}">
                            <td>
                                <input type="checkbox">
                            </td>
                            <td>
                                ${track.getTRACK_NUMBER()}.
                            </td>
                            <td>
                                ${artistName}
                            </td>
                            <td>
                                ${title}
                            </td>
                            <td>
                                <g:if test="${track.preview != null && !"null".equals(track.preview)}">
                                    <div class="btn btn-default btn-xs" onclick="playPause('${track.id}');">
                                        <small id="btn-${track.id}">▶</small>
                                    </div>
                                    <audio id="audio-${track.id}">
                                        <source src="${track.preview}" type="audio/mpeg">
                                        Your browser does not support the audio element.
                                    </audio>
                                </g:if>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    </g:each>
</div>
</body>
</html>
