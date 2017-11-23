<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-sm-6 col-sm-offset-3">
            <g:form controller="search" action="search" class="form-horizontal">
                <div class="form-group">
                    <label for="value" class="control-label col-sm-2">Search</label>

                    <div class="col-sm-6">
                        <input id="value" class="form-control" name="value" type="text" placeholder="Your search"
                               value="${searchTerm}">
                    </div>

                    <div class="col-sm-4">
                        <button type="submit" class="btn btn-default">Submit</button>
                    </div>
                </div>
            </g:form>
        </div>
    </div>

    <legend class="text-center">ARTISTS</legend>

    <g:each in="${artists.collate(6)}" var="artistLine">
        <div class="row">
            <g:each in="${artistLine}" var="artist">
                <div class="col-sm-2">
                    <g:link controller="artist" action="show" id="${artist.id}" class="nohover">
                        <img src="${artist.pictureMedium}" class="nopadding width-100">

                        <p class="text-center text-xsmall padding-top-5 black">
                            ${artist.name}
                        </p>
                    </g:link>
                </div>
            </g:each>
        </div>
    </g:each>

    <legend class="text-center">ALBUMS</legend>

    <g:each in="${albums.collate(6)}" var="albumLine">
        <div class="row">
            <g:each in="${albumLine}" var="album">
                <div class="col-sm-2">
                    <g:link controller="album" action="show" id="${album.id}" class="nohover">
                        <img src="${album.coverMedium}" class="nopadding width-100">

                        <p class="text-center text-xsmall padding-top-5 black">
                            ${album.title}
                            <br>
                            (${album.recordType} - ${album.nbTracks} tracks)
                        </p>
                    </g:link>
                </div>
            </g:each>
        </div>
    </g:each>

    <legend class="text-center">TITRES</legend>

    <table class="table">
        <thead>
        <tr>
            <th class="col-xs-1"></th>
            <th class="col-xs-3">Artist / Album</th>
            <th class="col-xs-6">Title</th>
            <th class="col-xs-2">Link</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tracksMap.keySet()}" var="key">
            <tr>
                <g:set var="tracks" value="${tracksMap.get(key)}"/>
                <g:set var="firstTrack" value="${tracks.first()}"/>
                <td>
                    <img src="${firstTrack.album.coverMedium}" class="nopadding width-100">
                </td>
                <td>
                    ${firstTrack.artist.name}
                    <br/>
                    <small class="text-muted">
                        ${firstTrack.album.title}
                    </small>
                    <br/>
                    <small class="text-muted text-xsmall">
                        <br/>
                        ${firstTrack.album.id}
                    </small>
                </td>
                <td>
                    <g:each in="${tracks}" var="track">
                        <p>
                            ${track.title}
                            <small class="text-muted text-xsmall">(${track.rank})</small>
                        </p>
                    </g:each>
                </td>
                <td>
                    <g:each in="${tracks}" var="track">
                        <g:link action="download" id="${track.id}"
                                params='[data: track.data, filename: "${track.artist.name} - ${track.title}.mp3"]'
                                class="btn btn-primary">
                            Click
                        </g:link>
                        <br/>
                    </g:each>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>
