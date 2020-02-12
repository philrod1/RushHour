package rushhour.tools;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rushhour.model.move.Move;
import rushhour.model.state.IntArrayState;
import rushhour.model.state.State;
import rushhour.solver.BreadthFirst;
import rushhour.solver.Solver;

public class GameParser {
	
	public static void main (String[] args) {
		for(int i = 80 ; i <= 160 ; i++) {
			System.out.print(i + ": ");
			parseString(getString(i), i);
		}
	}

	public GameParser() {

	}

	private static int[][] parseString(String s, int n) {
		State state = new IntArrayState();
		int carNr = 2;
		boolean ok = true;
		for (int i = 0; i < s.length() - 4; i += 4) {
			char type = s.charAt(i);
			int x = Integer.parseInt("" + s.charAt(i + 1));
			int y = Integer.parseInt("" + s.charAt(i + 2));
			int dir = Integer.parseInt("" + s.charAt(i + 3));
			
			if(type >= 'A' && type <= 'L') {
				if(!state.addVehicle(carNr++, new Point(x,y), 
						(dir==1) ? new Point (x,y+1) : new Point (x+1,y))) {
					System.out.println("Shit! " + type + new Point(x,y));
					ok = false;
					break;
				}
			} else if(type >= 'O' && type <= 'V') {
				if(!state.addVehicle(carNr++, new Point(x,y), (dir==1) ? new Point (x,y+2) : new Point (x+2,y))) {
					System.out.println("Shit! " + type + new Point(x,y));
					ok = false;
					break;
				}
			} else if(type >= 'W' && type <= 'Z') {
				if(!state.addVehicle(1, new Point(x,y), new Point (x+1,y))) {
					System.out.println("Shit! " + type + " " + new Point(x,y));
//					ok = false;
//					break;
				}
			}
		}
		
		if(!ok) {
			System.out.println("Fail.");
			state.print();
			return null;
		}
		
		int minMoves = (10 * Integer.parseInt("" + s.charAt(s.length() - 3)));
		minMoves += Integer.parseInt("" + s.charAt(s.length() - 2));
		minMoves++;
		char level = s.charAt(s.length() - 1);
		
		Solver solver = new BreadthFirst();
		List<Move> solution = solver.solve(state);
		String m = solver.getMessage();
		
		Pattern intsOnly = Pattern.compile("\\d+");
		Matcher makeMatch = intsOnly.matcher(m);
		makeMatch.find();
		String inputInt = makeMatch.group();
		int moves = Integer.parseInt(inputInt);
		String deck = null;
		String l = null;switch(level) {
		case 'B': l = " Beg "; break;
		case 'I': l = " Int "; break;
		case 'A': l = " Adv "; break;
		case 'E': l = " Exp "; break;
		case 'G': l = " Gen "; break;
		default: System.out.println("Fail on level"); return null;
		}
		if (n<41) {
			deck = "D1-";
		} else if (n<81) {
			deck = "D2-";
			n -= 40;
		} else if (n<121) {
			deck = "D3-";
			n -=80;
		} else {
			deck = "D4-";
			n -=120;
		}
		if (moves == minMoves) {
			
			System.out.println("Level " + level + ".  " + moves + "  (OK)");
			try {
				FileTools.saveFile(state.getGrid(), deck + n + l + moves + ".rh");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Level " + level + ".  " + moves + "  (Wrong number of moves)");
			try {
				FileTools.saveFile(state.getGrid(), deck + n + l + moves + ".rh");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//	System.out.println(solver.getMessage() + " (" + minMoves + ").  Level " + level);
		
		return state.getGrid();
	}

	private static String getString(int n) {
		switch (n) {
		case 1:
			return "X122A002O501P011Q311B041C442R252-07B";

		case 2:
			return "A001O302B311P511X022C421Q032D241E442F052K352-07B";

		case 3:
			return "X122O321A132P531B141C252-13B";

		case 4:
			return "O001P301X122A231Q332B541R252-08B";

		case 5:
			return "A002O301B501P011Q411X122K521R132D041E442F452-08B";

		case 6:
			return "A002B301C012O411P511X122Q321D032E231F041R352-08B";

		case 7:
			return "A101B202C401D501E311X122F521I232H341-12B";

		case 8:
			return "A302O501B212C411X022D221E321F032K432H042I241P342G052Q352-11B";

		case 9:
			return "A101B202C402D311E412X022O421F521P031Q132G241H541-11B";

		case 10:
			return "A002B201C402D012O511P021X122Q132E341F442G052H452-16B";

		case 11:
			return "O001A102P301X122B231Q332E541R252-24I";

		case 12:
			return "A001B102O501P211X022Q332C441R052-16I";

		case 13:
			return "A002B202C401D211O511E121X322P031F332G341H442I152J452-15I";

		case 14:
			return "A002B201C412D021E121X222F421G521H232I241J442K052-16I";

		case 15:
			return "A102B302C012D212O411P511Q021R121X222E231F331G442H152I352-22I";

		case 16:
			return "A002B202C401O501D011E212F121P221X322Q332G052-20I";

		case 17:
			return "A001O102B212C412X022D221E032P331Q042F441G541R052-23I";

		case 18:
			return "A002B201O301C012P021X122Q132D142R052-24I";

		case 19:
			return "A201B302E411D121X222E232F431O142-21I";

		case 20:
			return "A001O302B112C311X022D221P521E241F342Q352-09I";

		case 21:
			return "A002B201O301P011X122Q132R352-20A";

		case 22:
			return "A201O302B011P311C412X122D131E432F041G242H541Q152-25A";

		case 23:
			return "O202P501A211B312X322C231D331E432F442Q252-28A";

		case 24:
			return "A201B302C111D021X222E421F132O042G441H052-24A";

		case 25:
			return "A002B201C402D012O511P021X122E421Q132F141G341H442I452-26A";

		case 26:
			return "A101O302B011C311P411X122D521E031R132F241G541H352-27A";

		case 27:
			return "A001B102O301C112X022D221P521E332F241R352-27A";

		case 28:
			return "O002A301P211B412X022C031D131E332Q531R242F052G252-29A";

		case 29:
			return "O002P401A211X022B521C031D132E332F142K341H541R052-30A";

		case 30:
			return "O001A201P302B311X122C032D232Q531E052F252-31A";

		case 31:
			return "A002O302B311C412D021X122P521Q231E332F042R352-36E";

		case 32:
			return "A002O201B301C402X022D031E132F332P531G341H052-36E";

		case 33:
			return "A101R201B402X022I031D132E332P531F142G341H441Q052-39E";

		case 34:
			return "A001R302B311P511X022C421Q032D331E241F442I052H352-42E";

		case 35:
			return "O201A302P501B311X022K031Q132D142E341F441G052-42E";

		case 36:
			return "O001P102A402B111C212Q511X222R032D331E241F442G052-43E";

		case 37:
			return "A002B201C402D012O411P511Q021X122R132E341F442G052H452-46E";

		case 38:
			return "A001O302B112C311X022D221R521E332F241G342Q352-47E";

		case 39:
			return "A201O302B311X022C221R521D032E332F041G141H242I252-49E";

		case 40:
			return "O001A102B401C111D211P511X322Q032E331F241G442H052I352-50E";

		case 41:
			return "X122A102O301P511B021Q231C332D352-21I";

		case 42:
			return "X122A201O302P311B412C131D432E041F242G541Q152-21I";

		case 43:
			return "X222A002B201C111D312O511P021E131F232G241H341I442J052-22I";

		case 44:
			return "X022O002P301A401B501C211D421E032F232G531H041I342Q352-22I";

		case 45:
			return "X122A101O302P011B311C411D132E332Q531F241G342H052-23I";

		case 46:
			return "X122A201O302B012C411D021E131F231G332P531I342Q152-23I";

		case 47:
			return "X322O002A401B501C011P112Q221D521E032F341G442R052-24I";

		case 48:
			return "X322A102B302O501P012C221D032E331F432Q042G252H452-24I";

		case 49:
			return "X222O101A301B402C011D411E511F031G132H331I531J241P352-24I";

		case 50:
			return "X322A102B301C402D112E511O221F032P531G041H152I352-24I";

		case 51:
			return "X122A002B201C301D011E412F321G421H032I231J342K541Z052O252-24A";

		case 52:
			return "X022Z322A101O201B402C412P521D031Q132E142F341G452-29A";

		case 53:
			return "X022A001B102O301C401D221E131P332F241G342H541I052-25A";

		case 54:
			return "X022A001O202B501P221C321D421E521F032G041H141Q342I252J452-27A";

		case 55:
			return "X022A001O102P401Q112B221C321D432R242E541F352-27A";

		case 56:
			return "X122O001A101B201C302P501G411E321F231G432H341Q052-27A";

		case 57:
			return "X222A002B201O011C111P411Q511R132D341E442F052G452-27A";

		case 58:
			return "X322O001A102B301C401D111P511Q032E331F241G442H052I352-28A";

		case 59:
			return "X022Z052A001B102C402D112E312O511F421E131H232I241J341K442-28A";

		case 60:
			return "X022A101B202C401D211E511F032G231H332O531I041J342P252-29A";

		case 61:
			return "X222A001B102C301O501D111E421F032P231G331H442I052J352-29E";

		case 62:
			return "X022A001B202O401P112C221D321E432Q242F541R252-29E";

		case 63:
			return "X022Z541A101B202C402D212E511F221G321H432I041J242K441O152-30E";

		case 64:
			return "X022Z322A001B102C301D402E511F221H032I331J432G041K142O352-28E";

		case 65:
			return "X022A001B102C302D211O312P321E521F031G132H431I141J541Q252-31E";

		case 66:
			return "X122O001A101P202B501Q411C521D032E231R342F252G452-31E";

		case 67:
			return "X122O002A301B401C112P021D321E231F432G342H541Q052-31E";

		case 68:
			return "X022A102B302C501D211E311P521O031F231G332H342I152Q352-31E";

		case 69:
			return "X022A001O102B211P311C412D521E032F231G141H541Q252-32E";

		case 70:
			return "X022Z322A001B101C202O501P221E032F331G432H041I441Q152-33E";

		case 71:
			return "X022Z322A001B101C202D402E412O211P521F032G331H041I142Q352-37G";

		case 72:
			return "X022A101B202C402D211E312O511P031F132G331H442Q152-35G";

		case 73:
			return "X022A001O201B402C411P511D131Q232E041F341G442H152-37G";

		case 74:
			return "X022Z322O102A402B211C312D511E031G132H331I431P531J142K052Q252-39G";

		case 75:
			return "X022Z052O002A301B401C501D012E321F031G132H432I241J342K541-38G";

		case 76:
			return "X022A102B302O211C311D032E331F432G041H142I541P152-38G";

		case 77:
			return "X322O202P501A211B032C231D331E041F141G442Q252-41G";

		case 78:
			return "X022A101B202C402D311E412O421P031Q132R531F241G352-42G";

		case 79:
			return "X022A002B201C402D412E221G521F332O031H142I341K541J152-42G";

		case 80:
			return "X022Z322O002A301P501B211D131E232F432G041H241I442J452-51G";

		case 81:
			return "Y122A201B302C501D312E521F032G331H141O001P421Q352-00I";

		case 82:
			return "Y122A101B402C312D421E032F231G331H442I152J352O001P511-00I";

		case 83:
			return "X022A312B321C432D441O202P501Q211R142S152-00I";

		case 84:
			return "X022A501B212C221D332E141K252G452O102P401Q521R031S242-00I";

		case 85:
			return "Y022A402B031C231D332O301P511Q052-00I";

		case 86:
			return "Y122A131B232C432D241K442G052L341O002P501R021Q012-00I";

		case 87:
			return "X322A001B401C221D032E442O102P501Q112R331S052-00I";

		case 88:
			return "Y222A202B402C011D312E031F132G331H241I442J052K352O101P511-00I";

		case 89:
			return "Y022A031B131C231D341E442G152O002P501Q332-00I";

		case 90:
			return "X122A101B311C241D352O302P411Q021R132S531-00I";

		case 91:
			return "Y022A201B302C501D311E411F521G132H332I141J442K452O031-00A";

		case 92:
			return "Y222A002B201C301D011E412F521G042H541I052J252O032P431-00A";

		case 93:
			return "X222A102B111C421D241E052F452O001P302Q511R032S342-00A";

		case 94:
			return "X122A201B012C311D232E252F452O302P411Q511R021S131-00A";

		case 95:
			return "Y022A402B012C411D231E332F041O301P531Q252-00A";

		case 96:
			return "Y122A101B202C402D132E332F241G341H442I052O212P511Q021-00A";

		case 97:
			return "X122A112B321C032D342O001P102Q501R231S352-00A";

		case 98:
			return "X022A311B412C031D232E431F052O201P302Q531R142S252-00A";

		case 99:
			return "Y122A101B301C402D412E421F132G241H342I052O021P531-00A";

		case 100:
			return "X122A002B201C441D541O301P011Q332R042S052-00A";

		case 101:
			return "Y022A002B302C012D212E411F321G432H142I342J452O501P031Q152-00E";

		case 102:
			return "X122A101B501C212D521E032F231O001P202Q411R342S152-00E";

		case 103:
			return "Y122A101B301C402D421E032F231G342H052O001P531Q252-00E";

		case 104:
			return "Y022A101B202C401D501E311F521G032H231I332J041K341L152-00E";

		case 105:
			return "X022A001B221C321D432E541O102P401Q112R242S252-00E";

		case 106:
			return "X022A001B102C401D501E211F421O301P521Q132R242S352-00E";

		case 107:
			return "X122A101B311C232D241E342F052G452O001P202Q411R521-00E";

		case 108:
			return "X122A002B201C402D012E421F341G442H052I452O511P021Q132-00E";

		case 109:
			return "X322A401B501C011D221E521F032G442O002P112Q331R052-00E";

		case 110:
			return "X122A002B011Z322D032E231F432G041H152O202P501Q212R342S352-00E";

		case 111:
			return "X022A211B312C132D442O202P501Q321R031S352-00G";

		case 112:
			return "X322A301B011C112D032O002P501Q221R342S252-00G";

		case 113:
			return "Y022A201B302C501D321E132F141G342H252I452O411P521Q031-00G";

		case 114:
			return "X322A001B202C401D501E211F521G241H442I052O032P331-00G";

		case 115:
			return "X022A311B411C031D132E332F241G341H442I052O102P501-00G";

		case 116:
			return "X322A002B401C432D041E142F152O201P501Q331-00G";

		case 117:
			return "X322A002B202C401D501E012F521G241H341I442J052O021P232-00G";

		case 118:
			return "X122A101B202C402D212E321F241G342H352O001P411Q032R531-00G";

		case 119:
			return "X322A301B011C112D331E432F152G352O002P501Q221-00G";

		case 120:
			return "X022A002B201C402D412E221F521G332H142I341J541K152O031-00G";

		case 121:
			return "W122A201B011C412D031E132F431G141H242I541O302P311Q252-23I";

		case 122:
			return "W022A001B201C402D411E221F032G332H341I152J452O511P042-24I";

		case 123:
			return "W222A301B501C012D521E241F052G452O002P401Q021R342-24I";

		case 124:
			return "W122A001B201C302D312E021F321G432H042I341J052K452O501-24I";

		case 125:
			return "W122A101B202C402D132E332F241G341H442I052O011P511-24I";

		case 126:
			return "W022A101B201C312D511E221F032G431H531I041J142K452O302P331-25I";

		case 127:
			return "W022A101B402C412D031E142F341G452O201P521Q132-25I";

		case 128:
			return "W122A301B402C321D421E141F241G342O001P032Q531-25I";

		case 129:
			return "W122A302B112C311D231E331F432G041H152I352O002P501Q011-27I";

		case 130:
			return "W222A201B402C011D312E032F231G331H442I152J352O101P511-27I";

		case 131:
			return "W222A002B201C402D421E341F442G052O011P511Q132-28A";

		case 132:
			return "W022A001B211C412D131E432F242G441H541O102P311Q052-28A";

		case 133:
			return "W122A201B312C511D321E421F032G341H442I452O001P302-28A";

		case 134:
			return "W022A001B101C202D402E211F421G032H232I341J442K152O511-28A";

		case 135:
			return "W122A401B501C521D131E241F341G442H052I452O002P021Q232-29A";

		case 136:
			return "W022A301B412C031D131E332F052G252O002P211Q531R242-29A";

		case 137:
			return "W122A002B401C231D342E541O301P021Q332R052-29A";

		case 138:
			return "W322A002B301C011D112E032O511P221Q342R252-29A";

		case 139:
			return "W122A201B012C411D021E432F042G242H541I052J252O302P311-30A";

		case 140:
			return "W022A001B202C221D321E432F541O401P112Q242R252-29A";

		case 141:
			return "W222A102B301C011D111E421F031G132H331I241J442O501P352-29E";

		case 142:
			return "W122A101B202C402D311E412F241G342H052O021P132Q531-30E";

		case 143:
			return "W122A301B401C112D321E231F432G342H541O002P021Q052-31E";

		case 144:
			return "W022A001B102C302D211E521F031G132H431I141J541O312P321Q252-31E";

		case 145:
			return "W222A102B301C111D412E421F032G231H342I052J252O001P531-31E";

		case 146:
			return "W222A101B202C402D212E241F341G452O011P411Q511R132-32E";

		case 147:
			return "W022A101B202C402D311E412F421G241H342I352O521P031Q132-33E";

		case 148:
			return "W122A101B501C411D521E231F332G341H442O202P021Q052-33E";

		case 149:
			return "W122A102B301C402D112E321F032G231H342I541J152K352O001P411-33E";

		case 150:
			return "W122A101B202C402D311E032F231G342H152I352O001P411Q531-34E";

		case 151:
			return "W322A002B202C401D501E521F241G341H442I452O031P232-34G";

		case 152:
			return "W022A001B101C202D402E412F032G331H041I142O211P531Q152-35G";

		case 153:
			return "W022A301B402C411D031E132F332G142H341O201P531Q052-36G";

		case 154:
			return "W222A301B402C111D411E132F332G241H342I052J452O011P521-37G";

		case 155:
			return "W022A102B302C311D032E331F432G041H142I541O211P152-38G";

		case 156:
			return "W122A201B411C321D032E231F141G342O302P531Q252-38G";
		case 157:
			return "W322A211B312C021D231E331F432G442O202P501Q252-38G";
		case 158:
			return "W122A101B501C321D421E032F041G342O202P521Q231R352-39G";
		case 159:
			return "W022A001B102C301D402E332F341G442O211P511Q052-41G";
		case 160:
			return "W222A002B201C402D012E531F341G052H452O411P021Q132-44G";
		default:
			return "";
		}
	}
}
