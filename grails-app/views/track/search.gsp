<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
    <script>
        function playPause(id) {
            var player = $("#audio-" + id);
            if (player.prop("paused")) {
                var players = $(".player");
                var playing = $.grep(players, function (element, index) {
                    return $(element).prop("paused") === false;
                });
                $.map(playing, function (element) {
                    $(element).trigger("pause");
                });
                player.trigger("play");
            } else {
                player.trigger("pause");
            }
        }
    </script>
</head>

<body>
<div class="container">
    <table class="table">
        <g:each in="${tracks.collate(3)}" var="trackLine">
            <tr>
                <g:each in="${trackLine}" var="track">
                    <g:set var="artist" value="${track.artist.name}"/>
                    <g:set var="title" value="${track.title}"/>
                    <td class="col-sm-1">
                        <img src="${track.album.coverMedium}" class="sp-track-image"/>
                    </td>
                    <td class="col-sm-2">
                        <g:link controller="track" action="search" params="[artist: artist, title: title]">
                            ${artist} - ${title}
                        </g:link>
                    </td>
                    <td class="col-sm-1">
                        <g:link controller="search" action="download" id="${track.id}"
                                params='[data: track.data, filename: "${track.artist.name} - ${track.title}.mp3"]'
                                class="btn btn-primary">
                            <i class="glyphicon glyphicon-download"></i>
                            Download
                        </g:link>
                        <g:if test="${track.preview != null && !"null".equals(track.preview)}">
                            <div class="btn btn-default"
                                 onclick="playPause('${track.id}');">
                                <i class="glyphicon glyphicon-play"></i>
                            </div>
                            <audio id="audio-${track.id}" class="player">
                                <source src="${track.preview}" type="audio/mpeg">
                                Your browser does not support the audio element.
                            </audio>
                        </g:if>
                    </td>
                </g:each>
            </tr>
        </g:each>
    </table>
</div>
</body>
</html>