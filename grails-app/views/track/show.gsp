<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <h1>${track.artists.first().name} - ${track.name}</h1>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <g:link uri="${track.previewUrl}">Listen !</g:link>
            <br/>
            ${track.id}
            <br/>
            ${track.album.name}
            <br/>
            ${track.artists.collect { it.name }.join(",")}
            <br/>
            ${track.availableMarkets.join(",")}
            <br/>
            ${track.duration} ms
            <br/>
            ${track.explicit ? 'EXPLICIT LYRICS' : 'NORMAL'}
            <br/>
            ${track.href}
            <br/>
            ${track.popularity}
            <br/>
            ${track.trackNumber}
        </div>
    </div>
</div>
</body>
</html>
