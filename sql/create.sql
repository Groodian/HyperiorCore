CREATE SCHEMA IF NOT EXISTS hyperior_mc;

CREATE TABLE IF NOT EXISTS hyperior_mc.users_ban_history (
        id                  UUID NOT NULL PRIMARY KEY,
        target              UUID NOT NULL,
        created_by          UUID NOT NULL,
        type                INTEGER NOT NULL,
        reason              VARCHAR(256) NOT NULL,
        created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
        duration            INTERVAL
);

CREATE TABLE IF NOT EXISTS hyperior_mc.users (
        uuid                UUID NOT NULL PRIMARY KEY,
        name                VARCHAR(64) NOT NULL,
        rank                INTEGER NOT NULL,
        level               INTEGER NOT NULL,
        total_xp            INTEGER NOT NULL,
        coins               INTEGER NOT NULL,
        daily_bonus         TIMESTAMP WITH TIME ZONE,
        daily_bonus_vip     TIMESTAMP WITH TIME ZONE,
        ban                 UUID REFERENCES hyperior_mc.users_ban_history(id),
        logins              INTEGER NOT NULL,
        first_login         TIMESTAMP WITH TIME ZONE NOT NULL,
        last_login          TIMESTAMP WITH TIME ZONE NOT NULL,
        last_logout         TIMESTAMP WITH TIME ZONE,
        login_days          INTEGER NOT NULL,
        connection_time     INTEGER NOT NULL
);

-- CREATE TABLE IF NOT EXISTS data (UUID VARCHAR(100), playername VARCHAR(100), logins INT(100), firstlogin VARCHAR(100), lastlogin VARCHAR(100), lastlogout VARCHAR(100), lastip VARCHAR(100), logindays INT(100), connectiontime BIGINT(100))
-- CREATE TABLE IF NOT EXISTS ban (UUID VARCHAR(100), playername VARCHAR(100), ban VARCHAR(100), reason VARCHAR(100), history TEXT(99999), reports INT(100), reporthistory TEXT(99999))
-- CREATE TABLE IF NOT EXISTS stats (UUID VARCHAR(100), playername VARCHAR(100), points INT(100), playtime BIGINT(100), minigamesplayed INT(100), gamesplayed INT(100), gamesended INT(100), gamesfirst INT(100), gamessecond INT(100), gamesthird INT(100), gamesfourth INT(100), gamesfifth INT(100), minigamesfirst INT(100), minigamessecond INT(100), minigamesthird INT(100), minigamesfourth INT(100), minigamesfifth INT(100))
-- CREATE TABLE IF NOT EXISTS records (UUID VARCHAR(100), playername VARCHAR(100))
-- CREATE TABLE IF NOT EXISTS cosmetic (UUID VARCHAR(100), playername VARCHAR(100), cosmetics TEXT(99999), particle INT(100), block INT(100), helmet INT(100), chest_plate INT(100), pants INT(100), shoes INT(100), gadget INT(100))
