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

    <g:each in="${albums.collate(6)}" var="albumLine">
        <div class="row">
            <g:each in="${albumLine}" var="album">
                <div class="col-md-2">
                    <g:link controller="album" action="show" id="${album.id}" class="nohover">
                        <img src="${album.cover_medium}" class="nopadding width-100">

                        <p class="text-center text-xsmall padding-top-5 black">
                            ${album.title}
                            <br>
                            (${album.nbTracks} tracks)
                        </p>
                    </g:link>
                </div>
            </g:each>
        </div>
    </g:each>

    <table class="table">
        <thead>
        <tr>
            <td></td>
            <td>Artist</td>
            <td>Album</td>
            <td>Title</td>
            <td>Rank</td>
            <td>Link</td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tracks}" var="track">
            <tr>
                <td>
                    <img src="${track.album.cover_small}" class="nopadding width-100">
                </td>
                <td>${track.artist.name}</td>
                <td>${track.album.title}</td>
                <td>${track.title}</td>
                <td>${track.rank}</td>
                <td>
                    <g:link action="download" id="${track.id}"
                            params='[data: track.data, filename: "${track.artist.name} - ${track.title}.mp3"]'
                            class="btn btn-primary">
                        Click
                    </g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>
