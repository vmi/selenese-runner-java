var manager = new RollupManager();

manager.addRollupRule({
  name: 'do_login'
  , description: 'log in to localhost'
  , args: [
    {
      name: 'username'
      , description: 'username'
    }
    , {
      name: 'password'
      , description: 'password'
    }
  ], commandMatchers: [
  ]
  , getExpandedCommands: function(args) {
    var commands = [];
    commands.push({
      command: 'open'
      , target: '/rollup01.html'
    });
    commands.push({
      command: 'type'
      , target: 'username'
      , value: args.username
    });
    commands.push({
      command: 'type'
      , target: 'password'
      , value: args.password
    });
    commands.push({
      command: 'click'
      , target: 'id=login'
    });
    return commands;
  }
});
