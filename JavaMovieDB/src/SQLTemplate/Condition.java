package SQLTemplate;

import util.SQLUtil;

public class Condition {
    public enum Opt{
        /** Greater, Less, Not Greater, Not Less, Equal, Not Equal
         ** In, Not In, Exists, Not Exists.
         ** Like */
        G, L, NG, NL, E, NE, IN, NIN, EX, NEX, LI
    }
    // Attribute name.
    public String attri;
    // Condition option.
    public Opt opt;
    // Requirement.
    public String requi;

    public Condition(Opt opt, String attri, String requi) {
        this.attri = attri;
        this.opt = opt;
        this.requi = requi;
    }
    public Condition(Opt opt, String table, String attri, String requi) {
        this.attri = table + '.' + attri;
        this.opt = opt;
        this.requi = requi;
    }
    public Condition(Opt opt, SQLUtil.DataInfo di) {
        this.attri = di.attri_name;
        this.opt = opt;
        this.requi = SQLUtil.DataParser(di);
    }
    public Condition(Opt opt, String table, SQLUtil.DataInfo di) {
        this.attri = table + '.' + di.attri_name;
        this.opt = opt;
        this.requi = SQLUtil.DataParser(di);
    }
    public Condition(Opt opt, String attri, SelectT st) {
        this.attri = attri;
        this.opt = opt;
        this.requi = "(\n" + st.toSQL() + "\n)";
    }
    public Condition(Opt opt, String table, String attri, SelectT st) {
        this.attri = table + '.' + attri;
        this.opt = opt;
        this.requi = "(\n" + st.toSQL() + "\n)";
    }
    public Condition(Opt opt, String table_1, String attri_1, String table_2, String attri_2) {
        this.attri = table_1 + '.' + attri_1;
        this.opt = opt;
        this.requi = table_2 + '.' + attri_2;
    }

    @Override
    public String toString() {
        String ret = null;
        ret = this.attri +
            switch (this.opt) {
                case G -> " > ";
                case L -> " < ";
                case NG -> " <= ";
                case NL -> " >= ";
                case E -> " = ";
                case NE -> " != ";
                case IN -> " IN ";
                case NIN -> " NOT IN ";
                case EX -> " EXISTS ";
                case NEX -> " NOT EXISTS ";
                case LI -> " LIKE ";
            } +
            this.requi;
        return ret;
    }
}
