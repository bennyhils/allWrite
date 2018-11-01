function findUserByAddress(address, users) {
    for (let i = 0; i < users.length; ++i) {
        if (address == users[i].address) {
            return users[i];
        }
    }
    return 0;
}
function findFileById(id, files) {
    for (let i = 0; i < files.length; ++i) {
        if (id == files[i].id) {
            return files[i];
        }
    }
    return 0;
}
function formatSpeed(speed) {
    if (speed > 1024*1024) {
        return parseInt(speed/1024/1024) + " Мбайт/c";
    } else if (speed > 1024) {
        return parseInt(speed/1024) + " Кбайт/c";
    } else {
        return speed + " байт/c";
    }

}