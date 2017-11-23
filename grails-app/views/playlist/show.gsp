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
    <div class="row">
        <div class="col-md-12 text-center">
            <h1>${playlist.name}</h1>
        </div>
    </div>

    <table class="table">
        <g:each in="${tracks.collate(3)}" var="trackLine">
            <tr>
                <g:each in="${trackLine}" var="track">
                    <td class="col-sm-1">
                        <img src="${track.track.album.images.first().url}" class="sp-track-image"/>
                    </td>
                    <td class="col-sm-2">
                        ${track.track.artists.first().name} - ${track.track.name}
                    </td>
                    <td class="col-sm-1">
                        <g:if test="${track.track.previewUrl != null && !"null".equals(track.track.previewUrl)}">
                            <div class="btn btn-default"
                                 onclick="playPause('${track.track.id}');">
                                <i class="glyphicon glyphicon-play"></i>
                            </div>
                            <audio id="audio-${track.track.id}" class="player">
                                <source src="${track.track.previewUrl}" type="audio/mpeg">
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
