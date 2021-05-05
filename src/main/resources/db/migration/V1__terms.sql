
create type matchup as enum (
  'pvp', 'pvt', 'pvz',
  'tvp', 'tvt', 'tvz',
  'zvp', 'zvt', 'zvz'
);

create type unit as enum (
  -- protoss
  -- units
  'adept',
  'archon',
  'carrier'
  'colossus',
  'darkTemplar',
  'disruptor',
  'highTemplar',
  'immortal',
  'interceptor',
  'motherShip',
  'observer',
  'oracle',
  'phoenix',
  'probe',
  'sentry',
  'stalker',
  'tempest',
  'voidRay',
  'warpPrism',
  'zealot',

  -- building
  'assimilator',
  'cyberneticsCore',
  'darkShrine',
  'fleetBeacon',
  'forge',
  'gateway',
  'nexus',
  'photonCannon',
  'pylon',
  'roboticsBay',
  'roboticsFacility',
  'shieldBattery',
  'stargate',
  'stasisWard'
  'templarArchives',
  'twilightCouncil',
  'warpGate'

  -- upgrades
  'groundWeapons1',
  'groundWeapons2',
  'groundWeapons3',
  'groundArmor1',
  'groundArmor2',
  'groundArmor3',
  'shields1',
  'shields2',
  'shields3',
  'airWeapons1',
  'airWeapons2',
  'airWeapons3',
  'airArmor1',
  'airArmor2',
  'airArmor3',
  'charge',
  'fluxVanes',
  'graviticBoosters',
  'resonatingGlaives',
  'graviticDrive'
);

create table build (
  id varchar(20) primary key,
  matchups matchup[] not null,
  duration integer not null,
  thumbs_up integer not null,
  thumbs_down integer not null,
  dictation_file varchar not null,
  dictation_tg_id varchar
);

create table command (
  build_id varchar(20) references build(id),
  supply integer not null,
  minute integer not null,
  second integer not null,
  unit   unit not null
);

create table journal (
  user_id bigint not null,
  build_id varchar(20) references build(id),
  timestamp timestamp not null
);
