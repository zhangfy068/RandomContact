package cn.appleye.randomcontact.common.factory;

public class NicknameFactory implements IFactory{
	private static final String[] sNickNames = {
			"就当风没吹过我没来过","[命里犯贱]","清浅吟唱","抱歉，该昵称无法显示","巴黎铁塔下の那抹樱花","╬═爺ぁ低調☆","木槿昔年",
			"爱过人渣怪我眼瞎","祢的笑、明媚整个夏天","倾一世丶等一人","易如既往,为你峰狂","要么留、要么滚","世态炎凉狗也猖狂","抹不掉丶迩给俄的温柔",
			"穿着校服逛青楼","怕冷却爱上雪怕伤却爱上你","☆→哆啦卟懂A梦★べ","最深的爱往往最沉默","你的未来我预订了","半夏微凉、不言殇°","奈何桥上唱征服",
			"绝口不提当年的疯狂","ぴ破茧′幻化成蝶","不离不弃纯属放屁","你若暴尸街头我定拍手叫好","时光囚我终老","听够情话","温柔女人霸气范er",
			"时光偷走了上扬的嘴角√","╭華麗的沈默〆","半生毁","冷陌ら","﹏Smile、雨沫℡","心蛊.","心如柠檬天然酸ゞ","狐小狸°","无人囍","梦雪樱飞＆",
			"话不投机、聊你麻痹","再平凡咱也是限量版","唱一半的歌","懦弱给谁看","爱情不打烊","爱情终败给了时间","若只如初見","谁把流年搁浅","花楼雨谢",
			"卜壊的男亼","竹言墨语","网名正在编辑中……","磕磕绊绊从不言分离","鱼有一颗心仅有七秒情","若水清颜","纸飞机载走童年の梦","一般小伙一般傲",
			"街角dē风铃Ω","天塌下来老娘顶着","囿困囿","古城白衣少年殇","女子無情便是王","冷眼旁观装逼狗","斩不断情丝","じ凌乱の舞步、","时光仍在，你却不在",
			"泪ゞ湿了巴黎、","待我柔情似水淹死你可好","一盏清茶","因为有遗憾，所以叫青春","时光凉丷春衫薄","樱花雨落","一倾风月一流年","十里红妆桃花飘",
			"人比黄花瘦","盏茶作酒","听、这一季雨落","墨锦倾城染青衣","烟汐忆梦","那年╮迩笑靥如花","烟花巷陌*","白衣折扇翩翩少年","一饮茶尽","往事如煙、",
			"倾一世丶等一人","毒°罂粟ら","瑾色安年，谁许我一世荒芜","﹍習慣ろ單裑","月下独饮","我有故事你有酒么","弦已断，曲终散","尘已落定各自安","半盏流年",
			"再見亦是泪","花未眠","何须执手问年华","相思何处寄","月浅思念深","故巷笑别","墨雨无痕﹌","木槿暧夏七纪年","眉眼之间","姑且独酌饮","眉眼如初风华如故",
			"清樽独醉.","︶ㄣ素衣白裳ヾ","盏茶作酒","風吹柳絮飛","寂寞春宵锁梧桐","木槿","不思量、自难忘","朝成青丝暮成雪","﹏花開半夏°","百无一用是痴情",
			"白头偕老不是说说而已","凭栏、浅忆君","再回首ヅ我心依旧","心無所依°","练习忘记你","你的未来被我预定了","眉眼如故","贱人不贱渐渐贱","牽羊逛街的狼",
			"貧僧愛用海飛絲","゛让俄宠迩一辈了","伱是我左心房の風景","耳朵餓了想聽情話","眉眼如初歲月如故","╭吻爾之眸メ",
			"拐個公主回現代","百善笑為先","心冷誰暖","迩壹念之差我犯賤壹場","對不起，闖進妳的心","我有故事妳有酒麽","花開半夏涼城空","清風挽心ヽ","朕就是這樣的漢子",
			"心安伴我暖","願如初*","若再遇見只剩寒暄","心已鎖定","翻身的鹹魚","神經兮兮￢ε￢","流氓也是一种气质ヾ","怪我長的醜不入迩眼","打劫！交出棒棒糖",
			"淺藍淡雅ぴ","誓言淪為失言","壹曲琵琶半遮面シ","幻化成蝶","我與孤獨重歸於好","癮ì","淺笑ゞ傾城","你的未來我奉陪到底","七秒鍾の誋憶","加載中，請稍候……",
			"夢醒淚落","夕顏i","拼出壹個屬於我的未來","願負天下人不負妳","顧北清歌寒゜","じ渄筬芴擾ぐ","我迷了鹿","爺、獨手毀天下","逗比在此"
	};
	
	public static String createRandomNickName() {
		String nickName = "Hello kitty";
		
		int index = (int)(Math.random()*sNickNames.length);
		
		nickName = sNickNames[index];
		
		return nickName;
	}

	@Override
	public String createFirstRandomData() {
		return createRandomNickName();
	}

	@Override
	public String[] createFirstRandomData(int count, boolean repeatAllowed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createSecondRandomData() {
		// TODO Auto-generated method stub
		return null;
	}
}