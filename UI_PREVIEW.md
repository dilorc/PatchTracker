# DoseBatcher UI Preview

## Screen Layout

```
┌─────────────────────────────────────┐
│                                     │
│                                     │
│                                     │
│          Clicks: 5                  │  ← 48sp, Bold, Primary Color
│                                     │
│      Total Units: 2.5               │  ← 36sp, Medium, Secondary Color
│                                     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │                             │   │
│  │          CLICK              │   │  ← 120dp min height
│  │                             │   │  ← 32sp text, Bold
│  └─────────────────────────────┘   │  ← Full width button
│                                     │
│  ┌─────────────────────────────┐   │
│  │          Undo               │   │  ← 72dp height
│  └─────────────────────────────┘   │  ← 24sp text, Outlined
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

## Interaction Flow

### 1. Initial State
```
Clicks: 0
Total Units: 0.0
```

### 2. After Tapping CLICK 3 Times
```
Clicks: 3
Total Units: 1.5
Timer: 5 seconds countdown active
```

### 3. After Tapping Undo Once
```
Clicks: 2
Total Units: 1.0
Timer: Reset to 5 seconds
```

### 4. After 5 Seconds of Inactivity
```
Logcat: "Dose finalized: 2 clicks, 1.0 units at 1234567890"
Screen resets to:
Clicks: 0
Total Units: 0.0
```

## Material 3 Design

- **Primary Button**: Filled button with Material 3 styling
- **Outlined Button**: Outlined style for secondary action
- **Typography**: Material 3 default font with custom sizes
- **Colors**: Dynamic color scheme (adapts to system theme)
- **Spacing**: 24dp padding, 48dp between sections

## Optimizations for Fast Tapping

1. **Large Touch Target**: 120dp minimum height for CLICK button
2. **Full Width**: Button spans entire screen width
3. **Centered Layout**: Easy to reach with thumb
4. **Immediate Feedback**: State updates instantly on tap
5. **No Animations**: Fast, responsive UI without delays

## Accessibility

- Large text sizes (28sp+) for easy reading
- High contrast colors from Material 3
- Large touch targets (120dp+)
- Clear visual hierarchy

