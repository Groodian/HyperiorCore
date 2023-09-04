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

CREATE TABLE IF NOT EXISTS hyperior_mc.minecraft_party (
        uuid                UUID NOT NULL PRIMARY KEY,
        points              INTEGER NOT NULL,
        playtime            INTEGER NOT NULL,
        games_played        INTEGER NOT NULL,
        games_ended         INTEGER NOT NULL,
        mini_games_played   INTEGER NOT NULL,
        games_first         INTEGER NOT NULL,
        games_second        INTEGER NOT NULL,
        games_third         INTEGER NOT NULL,
        games_fourth        INTEGER NOT NULL,
        games_fifth         INTEGER NOT NULL,
        mini_games_first    INTEGER NOT NULL,
        mini_games_second   INTEGER NOT NULL,
        mini_games_third    INTEGER NOT NULL,
        mini_games_fourth   INTEGER NOT NULL,
        mini_games_fifth    INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS hyperior_mc.minecraft_party_records (
        uuid                UUID NOT NULL,
        name                VARCHAR(64) NOT NULL,
        record              INTEGER NOT NULL,
        achieved_at         TIMESTAMP WITH TIME ZONE NOT NULL,
        PRIMARY KEY (uuid, name)
);

CREATE TABLE IF NOT EXISTS hyperior_mc.cosmetics (
        uuid                UUID NOT NULL PRIMARY KEY,
        particle            INT,
        block               INT,
        helmet              INT,
        chest_plate         INT,
        pants               INT,
        shoes               INT,
        gadget              INT
);

CREATE TABLE IF NOT EXISTS hyperior_mc.cosmetics_items (
        uuid                UUID NOT NULL,
        id                  INT NOT NULL,
        unlocked_at         TIMESTAMP WITH TIME ZONE NOT NULL,
        duplicates          INTEGER NOT NULL,
        PRIMARY KEY (uuid, id)
);
