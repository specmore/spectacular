const presets = [
  [
    '@babel/env',
    {
      targets: {
        node: 'current',
      },
    },
  ],
  '@babel/preset-react',
  '@babel/typescript',
];

module.exports = { presets };
