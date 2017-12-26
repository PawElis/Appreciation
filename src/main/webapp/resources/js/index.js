/**
 * Created by Pavel on 04.12.2017.
 * active panel
 */
num_inset_motor=0;
flag_inset_motor=0;
time_pause_motor=1;
delta_x=10;
set_motor=false;
id_settimeout=0;
open_inset_num=0;
next_open_num=0;
width_header=0;

function click_header_inset(n_inset)
{

    if (!set_motor)
    {
        next_open_num=0;
        if (open_inset_num==0)
        {
            num_inset_motor=n_inset;
            flag_inset_motor=1;
            set_motor=true;
            motor_inset();
        }
        else
        {
            if (n_inset!=open_inset_num) next_open_num=n_inset;
            num_inset_motor=open_inset_num;
            flag_inset_motor=-1;
            set_motor=true;
            motor_inset();

        }
    }
    else
    {
        clearTimeout(id_settimeout);
        if (n_inset==num_inset_motor)
        {
            flag_inset_motor*=-1;
            motor_inset();
        }
        else
        {
            next_open_num=n_inset;
            if (flag_inset_motor>0)
            {
                flag_inset_motor=-1;

            }
            motor_inset();
        }
    }
}

function motor_inset()
{
    if (num_inset_motor==0) return;
    if (flag_inset_motor==0) return;
    obj_name="inset_num"+num_inset_motor;
    obj_inset=document.getElementById(obj_name);
    x_inset_motor=obj_inset.offsetLeft;

    x_inset_motor+=flag_inset_motor*delta_x;

    if (flag_inset_motor>0)
    {
        if (x_inset_motor>0)
        {
            x_inset_motor=0;
            set_motor=false;
            open_inset_num=num_inset_motor;
        }
    }
    else
    {
        if (x_inset_motor<=width_header-obj_inset.offsetWidth)
        {
            x_inset_motor=width_header-obj_inset.offsetWidth;
            set_motor=false;
            open_inset_num=0;
            if (next_open_num>0)
            {

                num_inset_motor=next_open_num;
                next_open_num=0;
                flag_inset_motor=1;
                set_motor=true;

            }
        }

    }
    obj_inset.style.left=x_inset_motor+"px";
    document.querySelector('div.reltr').style.left= x_inset_motor+ obj_inset.offsetWidth+ "px";
    if (set_motor)
    {
        id_settimeout=setTimeout("motor_inset()",time_pause_motor);
    }

}


