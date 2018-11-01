function findUserByAddress(address, users) {
    for (let i = 0; i < users.length; ++i) {
        if (address == users[i].address) {
            return users[i];
        }
    }
    return 0;
}